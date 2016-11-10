package ch.lgo.drinks.simple.service;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;

public interface IDrinksService {

	public DrinksDTOList getAll();

	public DrinkDTO loadById(long drinkId);

	public DrinkDTO createDrink(DrinkDTO newDrink);

	public DrinkDTO updateDrink(long drinkId, DrinkDTO updatedDrink);

}
