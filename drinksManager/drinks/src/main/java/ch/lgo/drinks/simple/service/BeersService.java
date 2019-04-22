package ch.lgo.drinks.simple.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import ch.lgo.drinks.simple.dto.BeerDataForEditDto;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.TapBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.HasBar;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.entity.TapBeer;
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
    
    private static Set<ForeignRelationConverter> converters;
    {
        converters = new HashSet<>();
        converters.add(new ForeignRelationConverter(
                beerDto -> Optional.ofNullable(beerDto.getColorId()),
                Beer::getColor,
                (beerEntity, newId) -> beersRepository.updateColorReference(beerEntity, newId)));
        converters.add(new ForeignRelationConverter(
                beerDto -> Optional.ofNullable(beerDto.getStyleId()),
                Beer::getStyle,
                (beerEntity, newId) -> beersRepository.updateStyleReference(beerEntity, newId)));
        converters.add(new ForeignRelationConverter(
                beerDto -> Optional.ofNullable(beerDto.getProducerId()), 
                Beer::getColor,
                (beerEntity, newId) -> beersRepository.updateProducerReference(beerEntity, newId)));
    }

    public Beer create(Beer newBeer) throws BadCreationRequestException {
        //TODO Add checks about beer consistency
        return beersRepository.save(newBeer);
    }

    public BeerDataForEditDto update(long beerId, BeerDataForEditDto updatedBeer) throws ResourceNotFoundException {
        //Load original entity
        // if empty : return 404
        Beer beerToUpdate = beersRepository.loadById(beerId)
                .orElseThrow(ResourceNotFoundException::new);
        
        //Replace all "simple values" by those from dto
        beerFieldsMapper.map(updatedBeer, beerToUpdate);
        
        //Replace foreign relations by new values "mocked"
        updateForeignRelations(updatedBeer, beerToUpdate);
        
        //Persist
        return beerFieldsMapper.map(beersRepository.save(beerToUpdate), BeerDataForEditDto.class);
    }

    private void updateForeignRelations(BeerDataForEditDto updatedBeer, Beer beerToUpdate) {
        //Detach all foreign relations to prevent stale id
        beersRepository.detachForeignRelations(beerToUpdate,
                converters.stream()
                    .map(ForeignRelationConverter::getEntityForeignEntityGetter)
                    .collect(Collectors.toSet()));
        //Replace actual foreign relations by mocked updated relations
        converters.stream().forEach(converter -> 
            converter.getEntityForeignEntitySetter().apply(beerToUpdate, 
                    converter.getDtoForeignIdGetter().apply(updatedBeer)));
    }
    
    private Set<Long> getUnAssociatedBars(BeerDataForEditDto updatedBeer, Beer beerToUpdate,
            Function<Beer, HasBar<?>> servingMethodGetterFromEntity, Function<BeerDataForEditDto, Set<Long>> barsIdsGetterFromDto) {
        return Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
                .map(HasBar::getBarsIds)
                .orElse(Collections.emptySet())
            .stream()
            .filter(barId -> !barsIdsGetterFromDto.apply(updatedBeer).contains(barId))
            .collect(Collectors.toSet());
    }
    
    private Set<Long> getNewlyAssociatedBars(BeerDataForEditDto updatedBeer, Beer beerToUpdate,
            Function<Beer, HasBar<?>> servingMethodGetterFromEntity, Function<BeerDataForEditDto, Set<Long>> barsIdsGetterFromDto) {
        return Optional.of(barsIdsGetterFromDto.apply(updatedBeer)).orElse(Collections.emptySet())
                .stream()
                .filter(barId -> !Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
                            .map(HasBar::getBarsIds)
                            .orElse(Collections.emptySet())
                        .contains(barId))
                .collect(Collectors.toSet());
    }
    
    private boolean fieldNeedsCreation(Beer beerToUpdate, BeerDataForEditDto updatedBeer, Function<Beer, ?> entityAttributeGetter, Set<Function<BeerDataForEditDto, ?>> dtoGetters) {
        return entityAttributeGetter.apply(beerToUpdate) == null && dtoGetters.stream().anyMatch(function -> function.apply(updatedBeer) != null); 
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
        return toDetailedDto(beersRepository.loadByIdWithServices(drinkId));
    }
    
    public Optional<BeerDataForEditDto> loadByIdForEdit(long drinkId) {
        return toVeryDetailedDto(beersRepository.loadById(drinkId));
    }
    
    public Optional<TapBeerDto> loadTapByIdForEdit(long drinkId) {
        return toTapBeerDto(beersRepository.loadTapByIdWithServices(drinkId));
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
    
    private Optional<DetailedBeerDto> toDetailedDto(Optional<Beer> beer) {
        return beer.map(beerToMap -> beerFieldsMapper.map(beerToMap, DetailedBeerDto.class));
    }
    
    private Optional<BeerDataForEditDto> toVeryDetailedDto(Optional<Beer> beer) {
        return beer.map(beerToMap -> beerFieldsMapper.map(beerToMap, BeerDataForEditDto.class));
    }
    
    private Optional<TapBeerDto> toTapBeerDto(Optional<TapBeer> beer) {
        return beer.map(beerToMap -> beerFieldsMapper.map(beerToMap, TapBeerDto.class));
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
    
    private static class ForeignRelationConverter {
        private Function<BeerDataForEditDto, Optional<Long>> dtoForeignIdGetter;
        private Function<Beer, HasId> entityForeignEntityGetter;
        private BiFunction<Beer, Optional<Long>, Beer> entityForeignEntitySetter;
        
        public Function<BeerDataForEditDto, Optional<Long>> getDtoForeignIdGetter() {
            return dtoForeignIdGetter;
        }
        public Function<Beer, HasId> getEntityForeignEntityGetter() {
            return entityForeignEntityGetter;
        }
        public BiFunction<Beer, Optional<Long>, Beer> getEntityForeignEntitySetter() {
            return entityForeignEntitySetter;
        }
        
        public ForeignRelationConverter(
                Function<BeerDataForEditDto, Optional<Long>> dtoForeignIdGetter,
                Function<Beer, HasId> entityForeignEntityGetter,
                BiFunction<Beer, Optional<Long>, Beer> entityForeignEntitySetter) {
            super();
            this.dtoForeignIdGetter = dtoForeignIdGetter;
            this.entityForeignEntityGetter = entityForeignEntityGetter;
            this.entityForeignEntitySetter = entityForeignEntitySetter;
        }
        
    }
}
