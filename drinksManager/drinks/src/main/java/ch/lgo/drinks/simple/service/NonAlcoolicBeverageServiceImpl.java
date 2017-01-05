package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.NonAlcoolicBeverageRepository;
import ch.lgo.drinks.simple.entity.NonAlcoolicBeverage;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Service
public class NonAlcoolicBeverageServiceImpl implements IDrinksService<NonAlcoolicBeverage>{

    @Autowired
    NonAlcoolicBeverageRepository nabRepository;
    
    public NonAlcoolicBeverage create(NonAlcoolicBeverage newNab) throws ResourceNotFoundException {
        return nabRepository.save(newNab);
    }

    public NonAlcoolicBeverage update(long nabId, NonAlcoolicBeverage updatedBeer)
            throws ResourceNotFoundException {
        NonAlcoolicBeverage drinkToUpdate = nabRepository.loadById(nabId);
        if (drinkToUpdate != null) {
            drinkToUpdate.setName(updatedBeer.getName());
            drinkToUpdate.setProducerName(updatedBeer.getProducerName());
            NonAlcoolicBeverage updatedDrink = nabRepository.save(drinkToUpdate);
            return updatedDrink;
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + nabId + " does not exists");
        }
    }
    
    @Override
    public List<NonAlcoolicBeverage> getAll() {
        return new ArrayList<>(nabRepository.findAll());
    }
    
    @Override
    public NonAlcoolicBeverage loadById(long drinkId) throws ResourceNotFoundException {
      NonAlcoolicBeverage nab = nabRepository.loadById(drinkId);
      if (nab != null) {
          return nab;
      } else {
          throw new ResourceNotFoundException(
                  "Drink of id " + drinkId + " does not exists");
      }
    }

    @Override
    public void delete(long drinkId) throws ResourceNotFoundException {
        if (nabRepository.exists(drinkId)) {
            nabRepository.delete(drinkId);
        } else {
            throw new ResourceNotFoundException(
                    "Drink of id " + drinkId + " does not exists");
        }
    }

    @Override
    public List<NonAlcoolicBeverage> findByName(String drinkName) {
        return new ArrayList<>(nabRepository.findByName(drinkName));
    }

}
