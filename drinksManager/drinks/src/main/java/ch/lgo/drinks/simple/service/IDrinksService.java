package ch.lgo.drinks.simple.service;

import java.util.List;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.exceptions.ResourceNotFound;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;

public interface IDrinksService {

	public List<Drink> getAll();

	public Drink loadById(long drinkId);

	public Drink createDrink(Drink newDrink) throws ResourceNotFound;

	public Drink updateDrink(long drinkId, Drink updatedDrink) throws ResourceNotFound;

	public void deleteDrink(long drinkId) throws ResourceNotFound;

	public List<Drink> findDrinksByType(String drinkTypeName) throws UnknownDrinkType;

	public List<Drink> findDrinksByName(String drinkName);

}