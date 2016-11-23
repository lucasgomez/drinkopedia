package ch.lgo.drinks.simple.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.IDrinkRepository;
import ch.lgo.drinks.simple.dao.IDrinkTypeRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkType;
import ch.lgo.drinks.simple.exceptions.ResourceNotFound;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;

@Service
public class DrinksServiceImpl implements IDrinksService {

	@Autowired
	IDrinkRepository drinkRepository;
	
	@Autowired
	IDrinkTypeRepository drinkTypeRepository;
	
	@Override
	public DrinksDTOList getAll() {
		Iterable<Drink> allDrinks = drinkRepository.findAll();
		return new DrinksDTOList(allDrinks);
	}

	@Override
	public DrinkDTO loadById(long drinkId) {
		Drink entity = drinkRepository.loadById(drinkId);
		if (entity != null)
			return new DrinkDTO(entity);
		else
			return null;
	}

	@Override
	public DrinkDTO createDrink(DrinkDTO newDrinkDTO) {
	    DrinkType drinkType = drinkTypeRepository.loadByName(newDrinkDTO.getType().getName());
		Drink drinkToCreate = new Drink(newDrinkDTO);
		drinkToCreate.setType(drinkType);
		
        Drink newDrink = drinkRepository.save(drinkToCreate);
		return new DrinkDTO(newDrink);
	}

	@Override
	public DrinkDTO updateDrink(long drinkId, DrinkDTO submittedDrinkUpdate) throws ResourceNotFound {
		Drink drinkToUpdate = drinkRepository.loadById(drinkId);
		if (drinkToUpdate != null) {
			drinkToUpdate.setName(submittedDrinkUpdate.getName());
			drinkToUpdate.setProducerName(submittedDrinkUpdate.getProducerName());
			Drink updatedDrink = drinkRepository.save(drinkToUpdate);
			return new DrinkDTO(updatedDrink);
		} else {
			throw new ResourceNotFound();
		}
	}

	@Override
	public void deleteDrink(long drinkId) throws ResourceNotFound {
		if (drinkRepository.exists(drinkId)) {
			drinkRepository.delete(drinkId);
		} else {
			throw new ResourceNotFound();
		}
	}

	@Override
	public DrinksDTOList findDrinksByType(String drinkTypeName) throws UnknownDrinkType {
		DrinkType type = drinkTypeRepository.loadByName(drinkTypeName);
		if (type == null) {
			throw new UnknownDrinkType();
		}
		
		return new DrinksDTOList(drinkRepository.findByType(type));
	}

	@Override
	public DrinksDTOList findDrinksByName(String drinkName) {
		List<Drink> foundDrinks = drinkRepository.findByName(drinkName);
		return new DrinksDTOList(foundDrinks);
	}
}
