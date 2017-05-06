package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class TapBeer implements IHasId {
	
	public static Long VOLUME_SMALL_CL= 33L;
	public static Long VOLUME_BIG_CL= 50L;

	private Long id;
	private Beer beer;
	private Double priceSmall;
	private Double priceBig;
	private Double buyingPricePerLiter;
	
	@Override
	@Id
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
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
