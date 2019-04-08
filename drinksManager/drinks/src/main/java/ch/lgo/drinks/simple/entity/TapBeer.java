package ch.lgo.drinks.simple.entity;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class TapBeer implements HasId {
	
	public static Long VOLUME_SMALL_CL= 30L;
	public static Long VOLUME_BIG_CL= 50L;

	private Long id;
	private Beer beer;
	private Double priceSmall;
	private Double priceBig;
	private Double buyingPricePerLiter;
	private Boolean pricesUpToDate;
	private Set<Bar> bars = new HashSet<>();
	
	private static Comparator<TapBeer> byPrice = comparing(TapBeer::getPriceBig, Comparator.nullsLast(Comparator.naturalOrder()));
	private static Comparator<TapBeer> byName = comparing(tap -> tap.getBeer().getName(), Comparator.nullsLast(Comparator.naturalOrder()));
	
	public TapBeer(Beer beer) {
	    this.setBeer(beer);
	}
	
    public TapBeer() {
    }

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

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="tapBeers")
	public Set<Bar> getBars() {
		return bars;
	}
	public TapBeer setBars(Set<Bar> bars) {
		this.bars = bars;
		return this;
	}
	
	public Boolean arePricesUpToDate() {
	    return pricesUpToDate;
	}
	public void setPricesUpToDate(Boolean pricesUpToDate) {
	    this.pricesUpToDate = pricesUpToDate;
	}
	
	public static Comparator<TapBeer> byPrice() {
		return byPrice;
	}
	public static Comparator<TapBeer> byName() {
		return byName;
	}
}
