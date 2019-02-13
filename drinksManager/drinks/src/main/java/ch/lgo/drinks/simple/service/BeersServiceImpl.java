package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Service
public class BeersServiceImpl {

    //TODO Check if not worth adding a "Rest Service" wrapper to manage REST specific behaviour such as ResourceNotFoundException and DTO translation
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
    ModelMapper modelMapper;

    public Beer create(Beer newBeer) throws BadCreationRequestException {
        //TODO Add checks about beer consistency
        return beersRepository.save(newBeer);
    }

    public Beer update(long beerId, Beer updatedBeer)
            throws ResourceNotFoundException, BadCreationRequestException {
        Beer beerToUpdate = beersRepository.loadById(beerId);
        if (beerToUpdate != null) {
            beerToUpdate.setName(updatedBeer.getName());
            beerToUpdate.setProducer(updatedBeer.getProducer());
            Beer updatedDrink = beersRepository.save(beerToUpdate);
            return updatedDrink;
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + beerId + " does not exists");
        }
    }

    public List<Beer> findByStyleId(long styleId) {
        return beersRepository.findByStyle(styleId);
    }

    public List<Beer> findByColorId(long colorId) {
        return beersRepository.findByColor(colorId);
    }

    public List<Beer> findByProducereId(long producerId) {
        return beersRepository.findByProducer(producerId);
    }

    public List<Beer> findByOriginId(long originId) {
        return beersRepository.findByOrigin(originId);
    }
    
    public List<Beer> findByBarId(long barId) {
        return barRepository.findBeersByBar(barId);
    }

    public List<Beer> getAll() {
        return new ArrayList<>(beersRepository.findAll());
    }

	public List<Beer> getAllWithService() {
		return beersRepository.findAllWithServices();
	}

    public Beer loadById(long drinkId) {
        return beersRepository.loadByIdWithServices(drinkId);
    }

    public void delete(long beerId) throws ResourceNotFoundException {
        if (beersRepository.exists(beerId)) {
            beersRepository.delete(beerId);
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + beerId + " does not exists");
        }
    }

    public List<Beer> findByName(String beerName) {
        return beersRepository.findByName(beerName);
    }

    private <E extends DescriptiveLabel> List<DescriptiveLabelDto> toSortedLabelList(Collection<E> labels) {
        return labels.stream()
            .sorted(DescriptiveLabel.byName)
            .map(label -> modelMapper.map(label, DescriptiveLabelDto.class))
            .collect(Collectors.toList());
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
}
