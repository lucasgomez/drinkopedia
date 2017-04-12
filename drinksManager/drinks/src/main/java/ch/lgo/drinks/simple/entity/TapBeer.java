package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TapBeer implements IHasId {
	
	private Long id;
	private Beer beer;
	private Double priceSmall;
	private Double priceBig;
	
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
	
	@ManyToOne
	public Beer getBeer() {
		return beer;
	}
	public void setBeer(Beer beer) {
		this.beer = beer;
	}

	public Double getPriceSmall() {
		return priceSmall;
	}
	public void setPriceSmall(Double priceSmall) {
		this.priceSmall = priceSmall;
	}
	
	public Double getPriceBig() {
		return priceBig;
	}
	public void setPriceBig(Double priceBig) {
		this.priceBig = priceBig;
	}
}
