package ch.lgo.drinks.simple.dto;

import ch.lgo.drinks.simple.entity.Drink;

public class DrinkDTO {

	private Long id;
	private String name;
	private String producerName;

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
	
	public DrinkDTO() {
	}
	
	public DrinkDTO(Drink drinkEntity) {
		this.id = drinkEntity.getId();
		this.name = drinkEntity.getName();
		this.producerName = drinkEntity.getProducerName();
	}

}
