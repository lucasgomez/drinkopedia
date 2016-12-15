package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dto.DrinkDTO;

@Entity
public class Drink {

	private Long id;
	private String name;
	private String producerName;
	private DrinkTypeEnum type;
	
	@Id
	@GeneratedValue
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

	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	@Enumerated(EnumType.STRING)
	public DrinkTypeEnum getType() {
		return type;
	}
	public void setType(DrinkTypeEnum type) {
		this.type = type;
	}
	
	public Drink() {
	}
		
	public Drink(String name) {
		this.name = name;
	}
	
	public Drink(DrinkDTO newDrinkDTO) {
		this.name = newDrinkDTO.getName();
		this.producerName = newDrinkDTO.getProducerName();
	}
	
	public Drink(String drinkName, String producerName, DrinkTypeEnum type) {
		this.name = drinkName;
		this.producerName = producerName;
		this.type = type;
	}
}
