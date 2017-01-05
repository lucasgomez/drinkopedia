package ch.lgo.drinks.simple.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.IDrinksRepository;
import ch.lgo.drinks.simple.dao.NonAlcoolicBeverageRepository;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Service
public class DrinksServiceImpl implements IDrinksService<Drink> {

	@Autowired
	BeersRepository beersRepository;
	@Autowired
	NonAlcoolicBeverageRepository nabsRepository;

    @Autowired
	Set<IDrinksRepository<?>> drinksRepositories;
	
	@Override
	public List<Drink> getAll() throws NoContentFoundException {
	    List<Drink> drinks = new ArrayList<>();
	    drinksRepositories.parallelStream().forEach(repo -> drinks.addAll(repo.findAll()));
	    return drinks;
	}

	@Override
	public Drink loadById(long drinkId) throws ResourceNotFoundException {
        List<Drink> drinks = new ArrayList<>();
        drinksRepositories.parallelStream().forEach(repo -> drinks.add(repo.loadById(drinkId)));
        
        if (drinks.isEmpty()) {
            throw new ResourceNotFoundException("No drink found with id "+drinkId);
        } else {
            //Case when more than 2 ignored as should never happen
            return drinks.get(0);
        }
	}

	@Override
	public void delete(long drinkId) throws ResourceNotFoundException {
	    List<Drink> all = new ArrayList<>();
        try {
            all = getAll();
        } catch (NoContentFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    all.forEach(drink -> System.out.println(drink.getDrinkType() + ") " + drink.getId() + " - " + drink.getName()));
	    nabsRepository.delete(drinkId);
	    all.forEach(drink -> System.out.println(drink.getDrinkType() + ") " + drink.getId() + " - " + drink.getName()));
	    beersRepository.delete(drinkId);
	    all.forEach(drink -> System.out.println(drink.getDrinkType() + ") " + drink.getId() + " - " + drink.getName()));
//	    drinksRepositories.forEach(repo -> repo.delete(drinkId));
	}

	@Override
	public List<Drink> findByName(String drinkName) {
        List<Drink> drinks = new ArrayList<>();
        drinksRepositories.parallelStream().forEach(repo -> drinks.addAll(repo.findByName(drinkName)));
        return drinks;
	}
	
	public List<Drink> findByType(DrinkTypeEnum drinkType) {
	    List<Drink> drinksFound = new ArrayList<>();
	    switch (drinkType) {
            case BEER :
                drinksFound.addAll(beersRepository.findAll());
                break;
            case NON_ALCOOLIC_BEVERAGE :
                drinksFound.addAll(nabsRepository.findAll());
                break;
            default :
                //TODO Complete with other drink types
                return Collections.emptyList();
        }
	    return drinksFound;
	}
	
	public DrinksServiceImpl() {
	}

}
