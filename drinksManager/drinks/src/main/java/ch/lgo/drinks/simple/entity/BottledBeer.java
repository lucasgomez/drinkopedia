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
public class BottledBeer implements HasId {

	private Long id;
	private Beer beer;
	private Long volumeInCl;
	private Double sellingPrice;
	private Double buyingPrice;
	private Set<Bar> bars = new HashSet<>();
	
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

	@OneToMany(mappedBy="beers")
	public Set<Bar> getBars() {
		return bars;
	}
	public BottledBeer setBars(Set<Bar> bars) {
		this.bars = bars;
		return this;
	}

}
