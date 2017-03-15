package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import ch.lgo.drinks.simple.dto.DrinkDTO;

@Entity
public abstract class Drink {

	private Long id;
	private String name;
	private Producer producer;
	private Set<Bar> bars = new HashSet<>();
	
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
	
	@ManyToOne
	public Producer getProducer() {
		return producer;
	}
	public void setProducer(Producer producer) {
		this.producer = producer;
	}
	
	public Drink() {
	}
		
	public Drink(String name) {
		this.name = name;
	}
	
	public Drink(DrinkDTO newDrinkDTO) {
		this.name = newDrinkDTO.getName();
	}
	
	@ManyToMany
	public Set<Bar> getBars() {
		return bars;
	}
	public void setBars(Set<Bar> bars) {
		this.bars = bars;
	}
	
	@Transient
	public abstract DrinkTypeEnum getDrinkType();
}
