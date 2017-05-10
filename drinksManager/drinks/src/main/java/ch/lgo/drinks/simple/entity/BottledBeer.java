package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class BottledBeer implements IHasId {

	private Long id;
	private Beer beer;
	private Long volumeInCl;
	private Double sellingPrice;
	private Double buyingPrice;
	
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
	
	public Long getVolumeInCl() {
		return volumeInCl;
	}
	public void setVolumeInCl(Long volume) {
		this.volumeInCl = volume;
	}
	
	public Double getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(Double price) {
		this.sellingPrice = price;
	}
	
	public Double getBuyingPrice() {
		return buyingPrice;
	}
	public void setBuyingPrice(Double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

}
