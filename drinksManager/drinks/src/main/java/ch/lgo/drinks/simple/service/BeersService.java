package ch.lgo.drinks.simple.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BarRepository;
import ch.lgo.drinks.simple.dao.BeerColorRepository;
import ch.lgo.drinks.simple.dao.BeerStylesRepository;
import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.dao.PlaceRepository;
import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.EntityManipulator;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.entity.StrengthEnum;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Service
public class BeersService {

    @Autowired
    BeersRepository beersRepository;
    @Autowired
    BeerColorRepository beerColorRepository;
    @Autowired
    BeerStylesRepository beerStylesRepository;
    @Autowired
    ProducerRepository producerRepository;
    @Autowired
    BeerStylesRepository beerStyleRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    BarRepository barRepository;
    @Autowired
    ColorService colorService;
    @Autowired
    ModelMapper beerFieldsMapper;
    
    private static Map<Function<DetailedBeerDto, String>, BiFunction<Beer, String, Beer>> strengthConverter;
    {
        strengthConverter = new HashMap<>();
        strengthConverter.put(DetailedBeerDto::getBitternessRank, (entity, stringValue) -> entity.setBitterness(StrengthEnum.getStrengthByRank(stringValue)));
        strengthConverter.put(DetailedBeerDto::getHoppingRank, (entity, stringValue) -> entity.setHopping(StrengthEnum.getStrengthByRank(stringValue)));
        strengthConverter.put(DetailedBeerDto::getSweetnessRank, (entity, stringValue) -> entity.setSweetness(StrengthEnum.getStrengthByRank(stringValue)));
        strengthConverter.put(DetailedBeerDto::getSournessRank, (entity, stringValue) -> entity.setSourness(StrengthEnum.getStrengthByRank(stringValue)));
    }
    private static Set<EntityManipulator<? extends HasId>> foreignEntitiesMapper;
    {
        foreignEntitiesMapper = new HashSet<>();
        foreignEntitiesMapper.add(new EntityManipulator<>(
                (dto) -> Optional.ofNullable(dto.getColorId()),
                Beer::getColor,
                (beer, id) -> beersRepository.updateColorReference(beer, id)));
        foreignEntitiesMapper.add(new EntityManipulator<>(
                (dto) -> Optional.ofNullable(dto.getStyleId()),
                Beer::getStyle,
                (beer, id) -> beersRepository.updateStyleReference(beer, id)));
        foreignEntitiesMapper.add(new EntityManipulator<>(
                (dto) -> Optional.ofNullable(dto.getProducerId()),
                Beer::getProducer,
                (beer, id) -> beersRepository.updateProducerReference(beer, id)));
    }

    public Beer create(Beer newBeer) throws BadCreationRequestException {
        //TODO Add checks about beer consistency
        return beersRepository.save(newBeer);
    }

    public Beer update(long beerId, DetailedBeerDto updatedBeer) throws ResourceNotFoundException {
        //Load original entity
        // if empty : return 404
        Beer beerToUpdate = beersRepository.loadById(beerId)
                .orElseThrow(ResourceNotFoundException::new);
        
        //List needed updaters for foreign entities
        Set<EntityManipulator<? extends HasId>> updaters = getUpdatersForForeignRelations(updatedBeer, beerToUpdate, foreignEntitiesMapper);

        //Then replace all "simple values" by those from dto
        beerFieldsMapper.map(updatedBeer, beerToUpdate);
        
        //Convert strengths values and replace
        convertStrength(updatedBeer, beerToUpdate);

        // and parse foreign entities, foreach :
        //      if FE id has changed then
        //          load said entity (or null)
        beersRepository.detachForeignReferences(beerToUpdate, updaters); 
        
        //Persist
        return beersRepository.save(beerToUpdate);
    }

    private Set<EntityManipulator<? extends HasId>> getUpdatersForForeignRelations(DetailedBeerDto updatedBeer, Beer beerToUpdate, 
            Set<EntityManipulator<? extends HasId>> foreignEntitiesMapper) {
        
        Set<EntityManipulator<? extends HasId>> manipulators = new HashSet<>();
        for (EntityManipulator<? extends HasId> entityManipulator : foreignEntitiesMapper) {
            if (hasForeignRelationChanged(updatedBeer, beerToUpdate, entityManipulator.getDtoToForeignEntityIdMapper(), entityManipulator.getForeignEntityIdGetter()))
                manipulators.add(entityManipulator.setNewId(entityManipulator.getDtoToForeignEntityIdMapper().apply(updatedBeer)));
        }
        return manipulators;
    }

    private boolean hasForeignRelationChanged(DetailedBeerDto updatedBeer, Beer beerToUpdate, 
            Function<DetailedBeerDto, Optional<Long>> dtoLongMapper, Function<Beer, Optional<Long>> foreignEntityIdGetter ) {
        return dtoLongMapper.apply(updatedBeer).get() != foreignEntityIdGetter.apply(beerToUpdate).get();
    }

    private void convertStrength(DetailedBeerDto updatedBeer,
            Beer beerToUpdate) {
        strengthConverter.entrySet().stream()
            .forEach(action -> mapStrength(updatedBeer, beerToUpdate, action.getKey(), action.getValue()));
    }

    private Beer mapStrength(DetailedBeerDto updatedBeer, Beer beerToUpdate, Function<DetailedBeerDto, String> strengthRankGetter, BiFunction<Beer, String, Beer> strengthRankSetter) {
        return strengthRankSetter.apply(beerToUpdate, strengthRankGetter.apply(updatedBeer));
    }

    public Optional<BeersDTOList> loadBeersByStyleId(long styleId) {
        return findByEntityId(styleId, beerStyleRepository::loadById, beersRepository::findByStyle);
    }

    public Optional<BeersDTOList> loadBeersByColorId(long colorId) {
        return findByEntityId(colorId, colorService::loadById, beersRepository::findByColor);
    }

    public Optional<BeersDTOList> loadBeersByProducereId(long producerId) {
        return findByEntityId(producerId, producerRepository::loadById, beersRepository::findByProducer);
    }

    public Optional<BeersDTOList> loadBeersByOriginId(long originId) {
        return findByEntityId(originId, placeRepository::loadById, beersRepository::findByOrigin);
    }
    
    public Optional<BeersDTOList> loadBeersByBarId(long barId) {
        return findByEntityId(barId, barRepository::loadById, barRepository::findBeersByBar);
    }

    public BeersDTOList getAll() {
        return convertToBeersListDTO(beersRepository.findAll());
    }

	public List<Beer> getAllWithService() {
		return beersRepository.findAllWithServices();
	}

    public Optional<DetailedBeerDto> loadById(long drinkId) {
        return Optional.of(toDetailedDto(beersRepository.loadByIdWithServices(drinkId)));
    }

    public void delete(long beerId) throws ResourceNotFoundException {
        if (beersRepository.exists(beerId)) {
            beersRepository.delete(beerId);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public BeersDTOList findByName(String beerName) {
        return convertToBeersListDTO(beersRepository.findByName(beerName));
    }

    public List<DescriptiveLabelDto> findColorsList() {
        return toSortedLabelList(beerColorRepository.findAllHavingService());
    }
    
    public List<DescriptiveLabelDto> findStylesList() {
        return toSortedLabelList(beerStyleRepository.findAllHavingService());
    }
    
    public List<DescriptiveLabelDto> findProducersList() {
        return toSortedLabelList(producerRepository.findAllHavingService());
    }
    
    public List<DescriptiveLabelDto> findPlacesList() {
        return toSortedLabelList(placeRepository.findAllHavingService());
    }
    
    public List<DescriptiveLabelDto> findBarsList() {
        return toSortedLabelList(barRepository.findAllHavingService());
    }
    
    private Optional<BeersDTOList> findByEntityId(long entityId, Function<Long, Optional<? extends DescriptiveLabel>> entityLoader, Function<Long, List<Beer>> beersLoader) {
        Optional<? extends DescriptiveLabel> entityLabel = entityLoader.apply(entityId);
        if (entityLabel.isPresent())
            return Optional.of(convertToBeersListDTO(beersLoader.apply(entityId), entityLabel.get()));
        else
            return Optional.empty();
    }
    
    private DetailedBeerDto toDetailedDto(Beer beer) {
        if (beer != null)
            return beerFieldsMapper.map(beer, DetailedBeerDto.class);
        else
            return null;
    }

    private <E extends DescriptiveLabel> List<DescriptiveLabelDto> toSortedLabelList(Collection<E> labels) {
        return labels.stream()
            .sorted(DescriptiveLabel.byName)
            .map(label -> beerFieldsMapper.map(label, DescriptiveLabelDto.class))
            .collect(Collectors.toList());
    }

    private BeersDTOList convertToBeersListDTO(Collection<Beer> beers) {
        return convertToBeersListDTO(beers, null);
    }
    
    private BeersDTOList convertToBeersListDTO(Collection<Beer> beers, DescriptiveLabel listDescription) {
        BeersDTOList beersDTOList = new BeersDTOList();
        List<BeerDTO> beersList = beers.stream().map(beer -> convertToDto(beer))
                .collect(Collectors.toList());
        beersDTOList.setBeers(beersList);
        if (listDescription != null) {
            beersDTOList.setName(listDescription.getName());
            beersDTOList.setDescription(listDescription.getComment());
        }
        return beersDTOList;
    }

    private BeerDTO convertToDto(Beer beer) {
        BeerDTO beerDTO = beerFieldsMapper.map(beer, BeerDTO.class);
        return beerDTO;
    }
}