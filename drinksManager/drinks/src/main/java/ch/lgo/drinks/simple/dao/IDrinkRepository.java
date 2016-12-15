package ch.lgo.drinks.simple.dao;

import java.util.List;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;

public interface IDrinkRepository {

	List<Drink> findByType(DrinkTypeEnum type);

	Drink loadById(long id);

	Iterable<Drink> findAll();

	List<Drink> findByName(String drinkName);

	void delete(long drinkId);

	boolean exists(long drinkId);

	Drink save(Drink drinkToUpdate);

	void deleteAll();
}
