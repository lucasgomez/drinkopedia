package ch.lgo.drinks.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.DrinkRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;

@Service
public class DrinksServiceImpl implements IDrinksService {

	@Autowired
	DrinkRepository drinkRepository;
	
	@Override
	public DrinksDTOList getAll() {
		Iterable<Drink> allDrinks = drinkRepository.findAll();
		return new DrinksDTOList(allDrinks);
	}

	@Override
	public DrinkDTO loadById(long drinkId) {
		Drink entity = drinkRepository.findOne(drinkId);
		if (entity != null)
			return new DrinkDTO(entity);
		else
			return null;
	}

	@Override
	public DrinkDTO createDrink(DrinkDTO newDrinkDTO) {
		Drink newDrink = drinkRepository.save(new Drink(newDrinkDTO));
		return new DrinkDTO(newDrink);
	}

	@Override
	public DrinkDTO updateDrink(long drinkId, DrinkDTO submittedDrinkUpdate) {
		Drink drinkToUpdate = drinkRepository.findOne(drinkId);
		if (drinkToUpdate != null) {
			drinkToUpdate.setName(submittedDrinkUpdate.getName());
			drinkToUpdate.setProducerName(submittedDrinkUpdate.getProducerName());
			Drink updatedDrink = drinkRepository.save(drinkToUpdate);
			return new DrinkDTO(updatedDrink);
		}
		//TODO replace null by exception throwing
		return null;
	}
}
