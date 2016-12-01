package ch.lgo.drinks.simple.dto.list;

import java.util.ArrayList;
import java.util.List;

import ch.lgo.drinks.simple.dto.DrinkDTO;

public class DrinksDTOList {
	
	private List<DrinkDTO> drinks;
	
	public List<DrinkDTO> getDrinks() {
		return drinks;
	}
	public void setDrinks(List<DrinkDTO> drinks) {
		this.drinks = drinks;
	}
	
	public DrinksDTOList() {
		this.drinks = new ArrayList<>();
	}
}
