package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dto.DrinkTypeDTO;

@Entity
public class DrinkType {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public DrinkType() {
	}
	
	public DrinkType(DrinkTypeDTO drinkTypeDTO) {
		this.name = drinkTypeDTO.getName();
	}
	
	public DrinkType(String name) {
		this.name = name;
	}
}
