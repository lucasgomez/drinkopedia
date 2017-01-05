package ch.lgo.drinks.simple.dao;

import java.util.Collection;

import ch.lgo.drinks.simple.entity.Drink;

public interface IDrinksRepository <T extends Drink> {

    T loadById(long id);

    boolean exists(long drinkId);
    
    Collection<T> findAll();

    Collection<T> findByName(String drinkName);

    void delete(long drinkId);

    void deleteAll();
}
