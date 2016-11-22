package ch.lgo.drinks.simple.service;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;

public interface IDrinksService {

	public DrinksDTOList getAll();

	public DrinkDTO loadById(long drinkId);

	public DrinkDTO createDrink(DrinkDTO newDrink);

	public DrinkDTO updateDrink(long drinkId, DrinkDTO updatedDrink);

	public void deleteDrink(long drinkId);

	public DrinksDTOList findDrinksByType(String drinkTypeName) throws UnknownDrinkType;

	public DrinksDTOList findDrinksByName(String drinkName);

}
