package ch.lgo.drinks.simple.dto;

public class DetailedPrintingDrinkDTO {
	
	private Long id;
	private Long beerId;
	private String beerName;
	private String beerProducerName;
	private String beerProducerOriginShortName;
    private Double beerAbv;
    private Long beerIbu;
    private Long beerSrm; 
	private Integer volumeInCl;
	private Double price;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getBeerId() {
		return beerId;
	}
	public void setBeerId(Long beerId) {
		this.beerId = beerId;
	}
	
	public String getBeerName() {
		return beerName;
	}
	public void setBeerName(String beerName) {
		this.beerName = beerName;
	}
	
	public String getBeerProducerName() {
		return beerProducerName;
	}
	public void setBeerProducerName(String beerProducerName) {
		this.beerProducerName = beerProducerName;
	}
	
	public String getBeerProducerOriginShortName() {
		return beerProducerOriginShortName;
	}
	public void setBeerProducerOriginShortName(String beerProducerOriginShortName) {
		this.beerProducerOriginShortName = beerProducerOriginShortName;
	}
	
	public Double getBeerAbv() {
		return beerAbv;
	}
	public void setBeerAbv(Double beerAbv) {
		this.beerAbv = beerAbv;
	}
	
	public Long getBeerIbu() {
		return beerIbu;
	}
	public void setBeerIbu(Long beerIbu) {
		this.beerIbu = beerIbu;
	}
	
	public Long getBeerSrm() {
		return beerSrm;
	}
	public void setBeerSrm(Long beerSrm) {
		this.beerSrm = beerSrm;
	}
	
	public Integer getVolumeInCl() {
		return volumeInCl;
	}
	public void setVolumeInCl(Integer volumeInCl) {
		this.volumeInCl = volumeInCl;
	}

	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
}
