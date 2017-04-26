package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BottledBeer implements IHasId {

	private Long id;
	private Beer beer;
	private Long volumeInCl;
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
	
	@OneToOne(optional=false)
	public Beer getBeer() {
		return beer;
	}
	public void setBeer(Beer beer) {
		this.beer = beer;
	}
	
	public Long getVolumeInCl() {
		return volumeInCl;
	}
	public void setVolumeInCl(Long volume) {
		this.volumeInCl = volume;
	}
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

}
