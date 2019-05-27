package ch.lgo.drinks.simple.service;

import java.time.LocalDateTime;
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
import ch.lgo.drinks.simple.dto.BeerWithPricesDto;
import ch.lgo.drinks.simple.dto.BottleBeerDto;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.TapBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Availability;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.HasBar;
import ch.lgo.drinks.simple.entity.HasBarsId;
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

    public TapBeerDto updateTap(long beerId, TapBeerDto updatedTap) throws ResourceNotFoundException {
        updatedTap.setBeerId(beerId);
        
        //Find referenced beer or else 404
        Beer beer = beersRepository.loadById(beerId).orElseThrow(ResourceNotFoundException::new);
        
        TapBeer tapToUpdate = beersRepository.loadTapByIdWithServices(beerId)
            .orElseGet(() -> beersRepository.save(new TapBeer(beer)));
        
        beerFieldsMapper.map(updatedTap, tapToUpdate);
        
        updateReferencedBars(updatedTap, tapToUpdate,
                (bar, tapBeer) -> bar.addTapBeer(tapBeer), (bar, tapBeer) -> bar.removeTapBeer(tapBeer));
        
        return beerFieldsMapper.map(beersRepository.save(tapToUpdate), TapBeerDto.class);
    }
    
    public BottleBeerDto updateBottle(long beerId, BottleBeerDto updatedBottle) throws ResourceNotFoundException {
        updatedBottle.setBeerId(beerId);
        
        //Find referenced beer or else 404
        Beer beer = beersRepository.loadById(beerId).orElseThrow(ResourceNotFoundException::new);
        
        BottledBeer bottleToUpdate = beersRepository.loadBottleByIdWithServices(beerId)
                .orElseGet(() -> beersRepository.save(new BottledBeer(beer)));
        
        beerFieldsMapper.map(updatedBottle, bottleToUpdate);
        
        updateReferencedBars(updatedBottle, bottleToUpdate,
                (bar, bottleBeer) -> bar.addBottledBeer(bottleBeer), (bar, bottleBeer) -> bar.removeBottledBeer(bottleBeer));
        
        return beerFieldsMapper.map(beersRepository.save(bottleToUpdate), BottleBeerDto.class);
    }

    private <D extends HasBar<D>> void updateReferencedBars(HasBarsId updatedServingMethod, D servingMethodToUpdate, 
            BiFunction<Bar, D, Bar> servingMethodAdder, BiFunction<Bar, D, Bar> servingMethodRemover) {

        //Remove un-associated bars
        servingMethodToUpdate.getBarsIds()
            .stream()
            .filter(barId -> !updatedServingMethod.getBarsIds().contains(barId))
            .map(barId -> barRepository.loadById(barId, true, true))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(bar -> servingMethodRemover.apply(bar, servingMethodToUpdate))
            .forEach(barRepository::save);
        
        //Add newly associated bars
        Optional.of(updatedServingMethod.getBarsIds()).orElse(Collections.emptySet())
            .stream()
            .filter(barId -> !Optional.ofNullable(servingMethodToUpdate)
                        .map(HasBar::getBarsIds)
                        .orElse(Collections.emptySet())
                    .contains(barId))
            .map(barId -> barRepository.loadById(barId, true, true))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(bar -> servingMethodAdder.apply(bar, servingMethodToUpdate))
            .forEach(barRepository::save);
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
    
    public Optional<BeersDTOList<BeerDTO>> loadBeersByStyleId(long styleId) {
        return findByEntityId(styleId, beerStyleRepository::loadById, beersRepository::findByStyle);
    }

    public Optional<BeersDTOList<BeerDTO>> loadBeersByColorId(long colorId) {
        return findByEntityId(colorId, colorService::loadById, beersRepository::findByColor);
    }

    public Optional<BeersDTOList<BeerDTO>> loadBeersByProducereId(long producerId) {
        return findByEntityId(producerId, producerRepository::loadById, beersRepository::findByProducer);
    }

    public Optional<BeersDTOList<BeerDTO>> loadBeersByOriginId(long originId) {
        return findByEntityId(originId, placeRepository::loadById, beersRepository::findByOrigin);
    }
    
    public Optional<BeersDTOList<BeerDTO>> loadBeersByBarId(long barId) {
        return findByEntityId(barId, barRepository::loadById, barRepository::findBeersByBar);
    }
    
    public Optional<BeersDTOList<BeerWithPricesDto>> loadBeersWithPricesByStyleId(long styleId) {
        return findByEntityIdWithPrices(styleId, beerStyleRepository::loadById, id -> beersRepository.findByStyle(id, false));
    }
    
    public Optional<BeersDTOList<BeerWithPricesDto>> loadBeersWithPricesByColorId(long colorId) {
        return findByEntityIdWithPrices(colorId, colorService::loadById, id -> beersRepository.findByColor(id, false));
    }
    
    public Optional<BeersDTOList<BeerWithPricesDto>> loadBeersWithPricesByProducereId(long producerId) {
        return findByEntityIdWithPrices(producerId, producerRepository::loadById, id -> beersRepository.findByProducer(id, false));
    }
    
    public Optional<BeersDTOList<BeerWithPricesDto>> loadBeersWithPricesByOriginId(long originId) {
        return findByEntityIdWithPrices(originId, placeRepository::loadById, id -> beersRepository.findByOrigin(id, false));
    }
    
    public Optional<BeersDTOList<BeerWithPricesDto>> loadBeersWithPricesByBarId(long barId) {
        return findByEntityIdWithPrices(barId, barRepository::loadById, barRepository::findBeersByBar);
    }

    public BeersDTOList<BeerDTO> getAll() {
        return convertToBeersListDTO(beersRepository.findAllWithServices(), null, this::convertToDto);
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
    
    public Optional<TapBeerDto> loadTapByBeerId(long drinkId) {
        return beersRepository.loadTapByIdWithServices(drinkId).map(tap -> beerFieldsMapper.map(tap, TapBeerDto.class));
    }
    
    public Optional<BottleBeerDto> loadBottleByBeerId(long drinkId) {
        return beersRepository.loadBottleByIdWithServices(drinkId).map(bottle -> beerFieldsMapper.map(bottle, BottleBeerDto.class));
    }
    
    public TapBeerDto loadTapByIdForEdit(long drinkId) {
        return beerFieldsMapper.map(beersRepository.loadTapByIdWithServices(drinkId).orElseGet(() -> new TapBeer()), TapBeerDto.class);
    }
    
    public BottleBeerDto loadBottleByIdForEdit(long drinkId) {
        return beerFieldsMapper.map(beersRepository.loadBottleByIdWithServices(drinkId).orElseGet(() -> new BottledBeer()), BottleBeerDto.class);
    }

    public void delete(long beerId) throws ResourceNotFoundException {
        if (beersRepository.exists(beerId)) {
            beersRepository.delete(beerId);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public BeersDTOList<BeerDTO> findByName(String beerName) {
        return convertToBeersListDTO(beersRepository.findByName(beerName), null, this::convertToDto);
    }

    public List<DescriptiveLabelDto> findColorsList(boolean havingServiceOnly) {
        return toSortedLabelList(havingServiceOnly ? beerColorRepository.findAllHavingService() : beerColorRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findStylesList(boolean havingServiceOnly) {
        return toSortedLabelList(havingServiceOnly ? beerStyleRepository.findAllHavingService() : beerStyleRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findProducersList(boolean havingServiceOnly) {
        return toSortedLabelList(havingServiceOnly ? producerRepository.findAllHavingService() : producerRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findPlacesList(boolean havingServiceOnly) {
        return toSortedLabelList(havingServiceOnly ? placeRepository.findAllHavingService() : placeRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findBarsList(boolean havingServiceOnly) {
        return toSortedLabelList(havingServiceOnly ? barRepository.findAllHavingService() : barRepository.findAll());
    }
    
    private Optional<BeersDTOList<BeerDTO>> findByEntityId(long entityId, Function<Long, Optional<? extends DescriptiveLabel>> labelEntityLoader, Function<Long, List<Beer>> beersLoader) {
        return findByEntityId(entityId, labelEntityLoader, beersLoader, this::convertToDto);
    }
    
    private Optional<BeersDTOList<BeerWithPricesDto>> findByEntityIdWithPrices(long entityId, Function<Long, Optional<? extends DescriptiveLabel>> labelEntityLoader, Function<Long, List<Beer>> beersLoader) {
        return findByEntityId(entityId, labelEntityLoader, beersLoader, this::convertToDtoWithPrices);
    }
    
    private <D> Optional<BeersDTOList<D>> findByEntityId(long entityId, Function<Long, Optional<? extends DescriptiveLabel>> labelEntityLoader, Function<Long, List<Beer>> beersLoader, Function<Beer, D> convertToDto) {
        return labelEntityLoader.apply(entityId)
                .map(label -> convertToBeersListDTO(beersLoader.apply(entityId), label, convertToDto));
    }
    
    private Optional<DetailedBeerDto> toDetailedDto(Optional<Beer> beer) {
        return beer.map(beerToMap -> beerFieldsMapper.map(beerToMap, DetailedBeerDto.class));
    }
    
    private Optional<BeerDataForEditDto> toVeryDetailedDto(Optional<Beer> beer) {
        return beer.map(beerToMap -> beerFieldsMapper.map(beerToMap, BeerDataForEditDto.class));
    }
    
    private <E extends DescriptiveLabel> List<DescriptiveLabelDto> toSortedLabelList(Collection<E> labels) {
        return labels.stream()
            .sorted(DescriptiveLabel.byName)
            .map(label -> beerFieldsMapper.map(label, DescriptiveLabelDto.class))
            .collect(Collectors.toList());
    }
    
    private <D> BeersDTOList<D> convertToBeersListDTO(Collection<Beer> beers, DescriptiveLabel listDescription, Function<Beer, D> dtoConverter) {
        BeersDTOList<D> beersDTOList = new BeersDTOList<>();
        List<D> beersList = beers.stream().map(dtoConverter::apply)
                .collect(Collectors.toList());
        beersDTOList.setBeers(beersList);
        if (listDescription != null) {
            beersDTOList.setName(listDescription.getName());
            beersDTOList.setDescription(listDescription.getComment());
        }
        return beersDTOList;
    }

    private BeerDTO convertToDto(Beer beer) {
        return beerFieldsMapper.map(beer, BeerDTO.class);
    }
    
    private BeerWithPricesDto convertToDtoWithPrices(Beer beer) {
        return beerFieldsMapper.map(beer, BeerWithPricesDto.class);
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

    public TapBeerDto updateTapAvailability(Long id, Availability beerAvailability) throws ResourceNotFoundException {
        TapBeer tap = beersRepository.loadTapByIdWithServices(id).orElseThrow(ResourceNotFoundException::new);
        
        if (tap.getAvailability() != beerAvailability) {
            tap.setAvailability(beerAvailability);
            tap.setAvailabilityDate(LocalDateTime.now());
            return beerFieldsMapper.map(beersRepository.save(tap), TapBeerDto.class);
        }
        return beerFieldsMapper.map(tap, TapBeerDto.class);
    }
    
    public BottleBeerDto updateBottleAvailability(Long id, Availability beerAvailability) throws ResourceNotFoundException {
        BottledBeer bottle = beersRepository.loadBottleByIdWithServices(id).orElseThrow(ResourceNotFoundException::new);
        
        if (bottle.getAvailability() != beerAvailability) {
            bottle.setAvailability(beerAvailability);
            return beerFieldsMapper.map(beersRepository.save(bottle), BottleBeerDto.class);
        }
        return beerFieldsMapper.map(bottle, BottleBeerDto.class);
    }
}
