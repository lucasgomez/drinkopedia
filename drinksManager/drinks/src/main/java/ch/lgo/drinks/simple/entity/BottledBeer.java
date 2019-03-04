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
public class BottledBeer implements HasId {

	private Long id;
	private Beer beer;
	private Long volumeInCl;
	private Double sellingPrice;
	private Double buyingPrice;
	private Boolean priceUpToDate;
	private Set<Bar> bars = new HashSet<>();
	
	private static Comparator<BottledBeer> byPrice = comparing(BottledBeer::getSellingPrice, Comparator.nullsLast(Comparator.naturalOrder()));
	private static Comparator<BottledBeer> byName = comparing(bottle -> bottle.getBeer().getName(), Comparator.nullsLast(Comparator.naturalOrder()));
	private static Comparator<BottledBeer> byStyle = comparing(bottle -> bottle.getBeer().getStyle().getName(), Comparator.nullsLast(Comparator.naturalOrder()));
	private static Comparator<BottledBeer> byColor = comparing(bottle -> bottle.getBeer().getColor().getName(), Comparator.nullsLast(Comparator.naturalOrder()));
	private static Comparator<BottledBeer> byProducer = comparing(bottle -> bottle.getBeer().getProducer().getName(), Comparator.nullsLast(Comparator.naturalOrder()));
	
	@Override
	@Id
	public Long getId() {
		return id;
	}
	@Override
	public BottledBeer setId(Long id) {
		this.id = id;
		return this;
	}
	
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
	public Beer getBeer() {
		return beer;
	}
	public BottledBeer setBeer(Beer beer) {
		this.beer = beer;
		return this;
	}
	
	public Long getVolumeInCl() {
		return volumeInCl;
	}
	public BottledBeer setVolumeInCl(Long volume) {
		this.volumeInCl = volume;
		return this;
	}
	
	public Double getSellingPrice() {
		return sellingPrice;
	}
	public BottledBeer setSellingPrice(Double price) {
		this.sellingPrice = price;
		return this;
	}
	
	public Double getBuyingPrice() {
		return buyingPrice;
	}
	public BottledBeer setBuyingPrice(Double buyingPrice) {
		this.buyingPrice = buyingPrice;
		return this;
	}

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="bottledBeer")
	public Set<Bar> getBars() {
		return bars;
	}
	public BottledBeer setBars(Set<Bar> bars) {
		this.bars = bars;
		return this;
	}

	public Boolean isPriceUpToDate() {
	    return priceUpToDate;
	}
	public void setPriceUpToDate(Boolean priceUpToDate) {
	    this.priceUpToDate = priceUpToDate;
	}
	
	public static Comparator<BottledBeer> byPrice() {
		return byPrice;
	}
	public static Comparator<BottledBeer> byName() {
		return byName;
	}
	public static Comparator<BottledBeer> byStyle() {
		return byStyle;
	}
	public static Comparator<BottledBeer> byColor() {
		return byColor;
	}
	public static Comparator<BottledBeer> byProducer() {
		return byProducer;
	}
	
}
