package ch.lgo.drinks.simple.dto.list;

import java.util.ArrayList;
import java.util.List;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.entity.Drink;

public class DrinksDTOList {
	
	private List<DrinkDTO> drinks;
	
	public List<DrinkDTO> getDrinks() {
		return drinks;
	}
	public void setDrinks(List<DrinkDTO> drinks) {
		this.drinks = drinks;
	}
	
	public DrinksDTOList(Iterable<Drink> allDrinks) {
		List<DrinkDTO> list = new ArrayList<>();
		for (Drink drinkEntity : allDrinks) {
			list.add(new DrinkDTO(drinkEntity));
		}
		this.drinks = list;
	}

}
