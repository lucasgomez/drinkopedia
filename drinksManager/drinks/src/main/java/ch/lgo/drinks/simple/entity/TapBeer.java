package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class TapBeer implements HasId {
	
	public static Long VOLUME_SMALL_CL= 33L;
	public static Long VOLUME_BIG_CL= 50L;

	private Long id;
	private Beer beer;
	private Double priceSmall;
	private Double priceBig;
	private Double buyingPricePerLiter;
	private Set<Bar> bars = new HashSet<>();
	
	@Override
	@Id
	public Long getId() {
		return id;
	}
	@Override
	public TapBeer setId(Long id) {
		this.id = id;
		return this;
	}
	
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
	public Beer getBeer() {
		return beer;
	}
	public TapBeer setBeer(Beer beer) {
		this.beer = beer;
		return this;
	}

	public Double getPriceSmall() {
		return priceSmall;
	}
	public TapBeer setPriceSmall(Double priceSmall) {
		this.priceSmall = priceSmall;
		return this;
	}
	
	public Double getPriceBig() {
		return priceBig;
	}
	public TapBeer setPriceBig(Double priceBig) {
		this.priceBig = priceBig;
		return this;
	}
	
	public Double getBuyingPricePerLiter() {
		return buyingPricePerLiter;
	}
	public TapBeer setBuyingPricePerLiter(Double buyingPricePerLiter) {
		this.buyingPricePerLiter = buyingPricePerLiter;
		return this;
	}

	@OneToMany(mappedBy="beers")
	public Set<Bar> getBars() {
		return bars;
	}
	public TapBeer setBars(Set<Bar> bars) {
		this.bars = bars;
		return this;
	}
}
