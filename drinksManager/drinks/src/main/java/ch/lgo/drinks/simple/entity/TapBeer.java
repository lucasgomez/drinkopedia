package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TapBeer implements IHasId {
	
	private Long id;
	private Beer beer;
	private Double priceSmall;
	private Double priceBig;
	private Double buyingPricePerLiter;
	
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
	
	public Double getBuyingPricePerLiter() {
		return buyingPricePerLiter;
	}
	public void setBuyingPricePerLiter(Double buyingPricePerLiter) {
		this.buyingPricePerLiter = buyingPricePerLiter;
	}
}
