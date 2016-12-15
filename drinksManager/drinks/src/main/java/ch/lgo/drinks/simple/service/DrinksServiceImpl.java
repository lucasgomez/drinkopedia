package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.IDrinkRepository;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;

@Service
public class DrinksServiceImpl implements IDrinksService {

	@Autowired
	IDrinkRepository drinkRepository;
	
	@Override
	public List<Drink> getAll() {
	    List<Drink> drinksList = new ArrayList<>();
	    drinkRepository.findAll().forEach(drink -> drinksList.add(drink));
        return drinksList;
	}

	@Override
	public Drink loadById(long drinkId) throws ResourceNotFoundException {
		Drink drink = drinkRepository.loadById(drinkId);
		if (drink != null) {
		    return drink;
		} else {
		    throw new ResourceNotFoundException("Drink of id " + drinkId + " does not exists");
		}
	}

	@Override
	public Drink createDrink(Drink drinkToCreate) throws ResourceNotFoundException {
        return drinkRepository.save(drinkToCreate);
	}

	@Override
	public Drink updateDrink(long drinkId, Drink submittedDrinkUpdate) throws ResourceNotFoundException {
		Drink drinkToUpdate = drinkRepository.loadById(drinkId);
		if (drinkToUpdate != null) {
			drinkToUpdate.setName(submittedDrinkUpdate.getName());
			drinkToUpdate.setProducerName(submittedDrinkUpdate.getProducerName());
			Drink updatedDrink = drinkRepository.save(drinkToUpdate);
			return updatedDrink;
		} else {
			throw new ResourceNotFoundException("Drink of id " + drinkId + " does not exists");
		}
	}

	@Override
	public void deleteDrink(long drinkId) throws ResourceNotFoundException {
		if (drinkRepository.exists(drinkId)) {
			drinkRepository.delete(drinkId);
		} else {
			throw new ResourceNotFoundException("Drink of id " + drinkId + " does not exists");
		}
	}

	@Override
	public List<Drink> findDrinksByType(DrinkTypeEnum drinkType) throws UnknownDrinkType {		
		return drinkRepository.findByType(drinkType);
	}

	@Override
	public List<Drink> findDrinksByName(String drinkName) {
		return drinkRepository.findByName(drinkName);
	}
}
