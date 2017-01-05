package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Service
public class BeersServiceImpl implements IDrinksService <Beer> {

    //TODO Check if not worth adding a "Rest Service" wrapper to manage REST specific behaviour such as ResourceNotFoundException and DTO translation
    @Autowired
    BeersRepository beersRepository;

    public Beer create(Beer newBeer) throws BadCreationRequestException {
        //TODO Add checks about beer consistency
        return beersRepository.save(newBeer);
    }

    public Beer update(long beerId, Beer updatedBeer)
            throws ResourceNotFoundException, BadCreationRequestException {
        Beer beerToUpdate = beersRepository.loadById(beerId);
        if (beerToUpdate != null) {
            beerToUpdate.setName(updatedBeer.getName());
            beerToUpdate.setProducerName(updatedBeer.getProducerName());
            Beer updatedDrink = beersRepository.save(beerToUpdate);
            return updatedDrink;
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + beerId + " does not exists");
        }
    }

    public List<Beer> findByStyles(Collection<BeerStyle> styles) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Beer> findByStyleId(long drinkTypeId) throws NoContentFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Beer> getAll() throws NoContentFoundException {
        List<Beer> beersFound = new ArrayList<>(beersRepository.findAll());
        
        if (!beersFound.isEmpty()) {
            return beersFound;
        } else {
            throw new NoContentFoundException();
        }
    }

    @Override
    public Beer loadById(long drinkId) throws ResourceNotFoundException {
        Beer beer = beersRepository.loadById(drinkId);
        if (beer != null) {
            return beer;
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + drinkId + " does not exists");
        }
    }

    @Override
    public void delete(long beerId) throws ResourceNotFoundException {
        if (beersRepository.exists(beerId)) {
            beersRepository.delete(beerId);
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + beerId + " does not exists");
        }
    }

    @Override
    public List<Beer> findByName(String beerName) throws NoContentFoundException {
        List<Beer> beersFound = new ArrayList<>(beersRepository.findByName(beerName));

        if (!beersFound.isEmpty()) {
            return beersFound;
        } else {
            throw new NoContentFoundException();
        }
    }

}
