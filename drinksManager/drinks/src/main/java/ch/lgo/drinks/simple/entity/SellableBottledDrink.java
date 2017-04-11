package ch.lgo.drinks.simple.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class SellableBottledDrink implements IHasId {

	private Long id;
	private Beer drink;
	private Integer volumeInCl;
	private Double price;
	
	@Override
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public Beer getDrink() {
		return drink;
	}
	public void setDrink(Beer beer) {
		this.drink = beer;
	}
	
	public Integer getVolumeInCl() {
		return volumeInCl;
	}
	public void setVolumeInCl(Integer volume) {
		this.volumeInCl = volume;
	}
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

}
