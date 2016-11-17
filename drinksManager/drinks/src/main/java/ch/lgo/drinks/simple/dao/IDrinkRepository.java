package ch.lgo.drinks.simple.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.lgo.drinks.simple.entity.Drink;

public interface IDrinkRepository extends CrudRepository<Drink, Long> {

	List<Drink> findByType_Name(String typeName);
}
