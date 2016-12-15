package ch.lgo.drinks.simple.dto;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;

public class DrinkDTO {

	private Long id;
	private String name;
	private String producerName;
	private DrinkTypeEnum type;

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
	
	public DrinkTypeEnum getType() {
		return type;
	}
	public void setType(DrinkTypeEnum type) {
		this.type = type;
	}
	
	public DrinkDTO() {
	}
	
	public DrinkDTO(Drink drinkEntity) {
		this.id = drinkEntity.getId();
		this.name = drinkEntity.getName();
		this.producerName = drinkEntity.getProducerName();
		if (drinkEntity.getType() != null) {
			this.type = drinkEntity.getType();
		}
	}

}
