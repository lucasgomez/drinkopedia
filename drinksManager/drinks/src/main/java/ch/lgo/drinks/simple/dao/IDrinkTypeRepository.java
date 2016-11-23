package ch.lgo.drinks.simple.dao;

import java.util.List;

import ch.lgo.drinks.simple.entity.DrinkType;

public interface IDrinkTypeRepository {

	List<DrinkType> findByName(String drinkTypeName);
	
	DrinkType loadByName(String drinkTypeName);

	DrinkType save(DrinkType drinkType);

	void deleteAll();

}
