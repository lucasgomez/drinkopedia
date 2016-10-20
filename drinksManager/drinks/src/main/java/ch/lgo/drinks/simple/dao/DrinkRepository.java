package ch.lgo.drinks.simple.dao;

import org.springframework.data.repository.CrudRepository;

import ch.lgo.drinks.simple.entity.Drink;

public interface DrinkRepository extends CrudRepository<Drink, Long> {

}
