package ch.lgo.drinks.simple.service;

import java.util.List;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

public interface IDrinksService <T extends Drink> {

	public List<T> getAll() throws NoContentFoundException;
                  
	public T loadById(long drinkId) throws ResourceNotFoundException;

	public void delete(long drinkId) throws ResourceNotFoundException;

	public List<T> findByName(String drinkName) throws NoContentFoundException;

}