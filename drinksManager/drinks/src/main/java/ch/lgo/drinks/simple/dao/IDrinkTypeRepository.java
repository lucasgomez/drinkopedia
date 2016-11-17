package ch.lgo.drinks.simple.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.lgo.drinks.simple.entity.DrinkType;

public interface IDrinkTypeRepository extends CrudRepository<DrinkType, Long> {

	List<DrinkType> findByName(String drinkTypeName);

}
