package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BeerColorRepository;
import ch.lgo.drinks.simple.dao.BeerStylesRepository;
import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.dao.PlaceRepository;
import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
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

    public List<Beer> findByStyleId(long styleId) throws NoContentFoundException {
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

    public List<Beer> getAll() throws NoContentFoundException {
        List<Beer> beersFound = new ArrayList<>(beersRepository.findAll());
        
        if (!beersFound.isEmpty()) {
            return beersFound;
        } else {
            throw new NoContentFoundException();
        }
    }

	public List<Beer> getAllWithService() {
		return beersRepository.findAllWithServices();
	}

    public Beer loadById(long drinkId) throws ResourceNotFoundException {
        Beer beer = beersRepository.loadById(drinkId);
        if (beer != null) {
            return beer;
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + drinkId + " does not exists");
        }
    }

    public void delete(long beerId) throws ResourceNotFoundException {
        if (beersRepository.exists(beerId)) {
            beersRepository.delete(beerId);
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + beerId + " does not exists");
        }
    }

    public List<Beer> findByName(String beerName) throws NoContentFoundException {
        List<Beer> beersFound = new ArrayList<>(beersRepository.findByName(beerName));

        if (!beersFound.isEmpty()) {
            return beersFound;
        } else {
            throw new NoContentFoundException();
        }
    }

    private <E extends DescriptiveLabel> List<DescriptiveLabelDto> toSortedLabelList(Collection<E> labels) {
        return labels.stream()
            .sorted(DescriptiveLabel.byName)
            .map(label -> modelMapper.map(label, DescriptiveLabelDto.class))
            .collect(Collectors.toList());
    }

    public List<DescriptiveLabelDto> findColorList() throws NoContentFoundException {
        return toSortedLabelList(beerColorRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findStyleList() {
        return toSortedLabelList(beerStyleRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findProducerList() {
        return toSortedLabelList(producerRepository.findAll());
    }
    
    public List<DescriptiveLabelDto> findPlaceList() {
        return toSortedLabelList(placeRepository.findAll());
    }
}
