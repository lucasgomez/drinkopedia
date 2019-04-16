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
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.VeryDetailedBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.HasBar;
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
                (beerEntity, newId) -> beersRepository.updateColorReference(beerEntity, newId)));
        converters.add(new ForeignRelationConverter(
                beerDto -> Optional.ofNullable(beerDto.getStyleId()), 
                (beerEntity, newId) -> beersRepository.updateStyleReference(beerEntity, newId)));
        converters.add(new ForeignRelationConverter(
                beerDto -> Optional.ofNullable(beerDto.getProducerId()), 
                (beerEntity, newId) -> beersRepository.updateProducerReference(beerEntity, newId)));
    }
    private static Set<Function<VeryDetailedBeerDto, ?>> dtoTapFieldsGetters;
    {
        dtoTapFieldsGetters = new HashSet<>();
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapBuyingPricePerLiter);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapPriceBig);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapPriceSmall);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapBarsIds);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapAssortment);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getTapAvailability);
    }
    private static Set<Function<VeryDetailedBeerDto, ?>> dtoBottleFieldsGetters;
    {
        dtoTapFieldsGetters = new HashSet<>();
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getBottleBuyingPrice);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getBottleSellingPrice);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getBottleVolumeInCl);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getBottleBarsIds);
        dtoTapFieldsGetters.add(VeryDetailedBeerDto::getBottleAvailability);
    }

    public Beer create(Beer newBeer) throws BadCreationRequestException {
        //TODO Add checks about beer consistency
        return beersRepository.save(newBeer);
    }

    public VeryDetailedBeerDto update(long beerId, VeryDetailedBeerDto updatedBeer) throws ResourceNotFoundException {
        //Load original entity
        // if empty : return 404
        Beer beerToUpdate = beersRepository.loadById(beerId)
                .orElseThrow(ResourceNotFoundException::new);
        
        //Then replace all "simple values" by those from dto
        beerFieldsMapper.map(updatedBeer, beerToUpdate);
        
        if (fieldNeedsCreation(beerToUpdate, updatedBeer, Beer::getTap, dtoTapFieldsGetters)) {
            beerToUpdate.setTap(new TapBeer(beerToUpdate));
            beersRepository.save(beerToUpdate.getTap());
        }
        if (fieldNeedsCreation(beerToUpdate, updatedBeer, Beer::getBottle, dtoBottleFieldsGetters)) {
            beerToUpdate.setBottle(new BottledBeer(beerToUpdate));
            beersRepository.save(beerToUpdate.getBottle());
        }

        converters.stream().forEach(converter -> 
            converter.getEntityForeignEntitySetter().apply(beerToUpdate, 
                    converter.getDtoForeignIdGetter().apply(updatedBeer)));
        
//        updateReferencedBars(updatedBeer, beerToUpdate, Beer::getTap, VeryDetailedBeerDto::getTapBarsIds, beer -> beersRepository.save(beer.getTap()));
        updateReferencedBars(updatedBeer, beerToUpdate, Beer::getBottle, VeryDetailedBeerDto::getBottleBarsIds, beer -> beersRepository.save(beer.getBottle()), 
                (bar, bottledBeer) -> bar.addBottledBeer((BottledBeer) bottledBeer));
        
        // and parse foreign entities, foreach :
        //      if FE id has changed then
        //          load said entity (or null)
        
        //Persist
        return beerFieldsMapper.map(beerToUpdate, VeryDetailedBeerDto.class);
//        return beerFieldsMapper.map(beersRepository.save(beerToUpdate), VeryDetailedBeerDto.class);
    }

    private void updateReferencedBars(VeryDetailedBeerDto updatedBeer, Beer beerToUpdate, Function<Beer, HasBar<?>> servingMethodGetterFromEntity, 
            Function<VeryDetailedBeerDto, Set<Long>> barsIdsGeterFromDto, Function<Beer, HasBar<?>> persister,
            BiFunction<Bar, HasBar<?>, Bar> serviceAdder) {
        
        //Remove un-associated bars
        Set<Long> deletedBars = Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
                .map(HasBar::getBarsIds)
                .orElse(Collections.emptySet())
            .stream()
            .filter(barId -> !barsIdsGeterFromDto.apply(updatedBeer).contains(barId))
            .collect(Collectors.toSet());
        Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
                .map(HasBar::getBarsIds)
                .orElse(Collections.emptySet())
            .stream()
            .filter(barId -> !barsIdsGeterFromDto.apply(updatedBeer).contains(barId))
            .collect(Collectors.toSet()).stream() //To prevent concurent modification
            .forEach(barToRemove -> servingMethodGetterFromEntity.apply(beerToUpdate).getBars().remove(barToRemove));

        //Add newly associated bars
        Set<Bar> newBars = Optional.of(barsIdsGeterFromDto.apply(updatedBeer)).orElse(Collections.emptySet())
        .stream()
        .filter(barId -> !Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
                    .map(HasBar::getBarsIds)
                    .orElse(Collections.emptySet())
                .contains(barId))
        .map(barId -> barRepository.loadById(barId, true, true))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
        
        newBars.stream()
            .map(bar -> serviceAdder.apply(bar, servingMethodGetterFromEntity.apply(beerToUpdate)))
            .forEach(barRepository::save);
        
//        Optional.of(barsIdsGeterFromDto.apply(updatedBeer)).orElse(Collections.emptySet())
//            .stream()
//            .filter(barId -> !Optional.ofNullable(servingMethodGetterFromEntity.apply(beerToUpdate))
//                        .map(HasBar::getBarsIds)
//                        .orElse(Collections.emptySet())
//                    .contains(barId))
//            .map(barRepository::getBarReference)
//            .forEach(bar -> servingMethodGetterFromEntity.apply(beerToUpdate).getBars().add(bar));
//
//        persister.apply(beerToUpdate);
    }

    private boolean fieldNeedsCreation(Beer beerToUpdate, VeryDetailedBeerDto updatedBeer, Function<Beer, ?> entityAttributeGetter, Set<Function<VeryDetailedBeerDto, ?>> dtoGetters) {
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
        return Optional.of(toDetailedDto(beersRepository.loadByIdWithServices(drinkId)));
    }
    
    public Optional<VeryDetailedBeerDto> loadByIdForEdit(long drinkId) {
        return Optional.of(toVeryDetailedDto(beersRepository.loadByIdWithServices(drinkId)));
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
    
    private VeryDetailedBeerDto toVeryDetailedDto(Beer beer) {
        if (beer != null)
            return beerFieldsMapper.map(beer, VeryDetailedBeerDto.class);
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
    
    private static class ForeignRelationConverter {
        private Function<BeerDTO, Optional<Long>> dtoForeignIdGetter;
        private BiFunction<Beer, Optional<Long>, Beer> entityForeignEntitySetter;
        
        public Function<BeerDTO, Optional<Long>> getDtoForeignIdGetter() {
            return dtoForeignIdGetter;
        }
        public BiFunction<Beer, Optional<Long>, Beer> getEntityForeignEntitySetter() {
            return entityForeignEntitySetter;
        }
        
        public ForeignRelationConverter(
                Function<BeerDTO, Optional<Long>> dtoForeignIdGetter,
                BiFunction<Beer, Optional<Long>, Beer> entityForeignEntitySetter) {
            super();
            this.dtoForeignIdGetter = dtoForeignIdGetter;
            this.entityForeignEntitySetter = entityForeignEntitySetter;
        }
        
    }
}
