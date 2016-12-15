package ch.lgo.drinks.simple.service;

import java.util.List;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;

public interface IDrinksService {

	public List<Drink> getAll();

	public Drink loadById(long drinkId) throws ResourceNotFoundException;

	public Drink createDrink(Drink newDrink) throws ResourceNotFoundException;

	public Drink updateDrink(long drinkId, Drink updatedDrink) throws ResourceNotFoundException;

	public void deleteDrink(long drinkId) throws ResourceNotFoundException;

	public List<Drink> findDrinksByType(DrinkTypeEnum drinkType) throws UnknownDrinkType;

	public List<Drink> findDrinksByName(String drinkName);

}