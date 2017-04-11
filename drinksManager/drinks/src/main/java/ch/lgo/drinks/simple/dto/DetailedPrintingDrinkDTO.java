package ch.lgo.drinks.simple.dto;

public class DetailedPrintingDrinkDTO {
	
	private Long id;
	private Long beerId;
	private String drinkName;
	private String drinkProducerName;
	private String drinkProducerOriginName;
	private String drinkProducerOriginShortName;
    private Double drinkAbv;
    private Long drinkIbu;
    private Long drinkSrm; 
	private Integer volumeInCl;
	private Double price;
	private String drinkComment;
	
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
	
	public String getDrinkName() {
		return drinkName;
	}
	public void setDrinkName(String beerName) {
		this.drinkName = beerName;
	}

	public String getDrinkProducerName() {
		return drinkProducerName;
	}
	public void setDrinkProducerName(String drinkProducerName) {
		this.drinkProducerName = drinkProducerName;
	}
	
	public String getDrinkProducerOriginName() {
		return drinkProducerOriginName;
	}
	public void setDrinkProducerOriginName(String drinkProducerOriginName) {
		this.drinkProducerOriginName = drinkProducerOriginName;
	}
	
	public String getDrinkProducerOriginShortName() {
		return drinkProducerOriginShortName;
	}
	public void setDrinkProducerOriginShortName(String drinkProducerOriginShortName) {
		this.drinkProducerOriginShortName = drinkProducerOriginShortName;
	}
	
	public Double getDrinkAbv() {
		return drinkAbv;
	}
	public void setDrinkAbv(Double drinkAbv) {
		this.drinkAbv = drinkAbv;
	}
	
	public Long getDrinkIbu() {
		return drinkIbu;
	}
	public void setDrinkIbu(Long drinkIbu) {
		this.drinkIbu = drinkIbu;
	}
	
	public Long getDrinkSrm() {
		return drinkSrm;
	}
	public void setDrinkSrm(Long drinkSrm) {
		this.drinkSrm = drinkSrm;
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

	public String getDrinkComment() {
		return drinkComment;
	}
	public void setDrinkComment(String drinkComment) {
		this.drinkComment = drinkComment;
	}
}
