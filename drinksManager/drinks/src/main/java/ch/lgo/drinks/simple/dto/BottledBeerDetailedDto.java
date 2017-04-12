package ch.lgo.drinks.simple.dto;

import ch.lgo.drinks.simple.entity.FermentingEnum;

public class BottledBeerDetailedDto {
	
	private Long id;
	private String name;
	private String producerName;
	private String producerOriginName;
	private String producerOriginShortName;
    private Double abv; //Alcool
    private Long ibu; //Bitterness
    private Long srm; //Color 
    private Long plato;
	private String styleName;
    private FermentingEnum fermenting;
	private String comment;
	private Long bottleVolumeInCl;
	private Double bottlePrice;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	public String getProducerOriginName() {
		return producerOriginName;
	}
	public void setProducerOriginName(String producerOriginName) {
		this.producerOriginName = producerOriginName;
	}
	public String getProducerOriginShortName() {
		return producerOriginShortName;
	}
	public void setProducerOriginShortName(String producerOriginShortName) {
		this.producerOriginShortName = producerOriginShortName;
	}
	public Double getAbv() {
		return abv;
	}
	public void setAbv(Double abv) {
		this.abv = abv;
	}
	public Long getIbu() {
		return ibu;
	}
	public void setIbu(Long ibu) {
		this.ibu = ibu;
	}
	public Long getSrm() {
		return srm;
	}
	public void setSrm(Long srm) {
		this.srm = srm;
	}
	public Long getPlato() {
		return plato;
	}
	public void setPlato(Long plato) {
		this.plato = plato;
	}
	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	public FermentingEnum getFermenting() {
		return fermenting;
	}
	public void setFermenting(FermentingEnum fermenting) {
		this.fermenting = fermenting;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getBottleVolumeInCl() {
		return bottleVolumeInCl;
	}
	public void setBottleVolumeInCl(Long bottleVolumeInCl) {
		this.bottleVolumeInCl = bottleVolumeInCl;
	}
	public Double getBottlePrice() {
		return bottlePrice;
	}
	public void setBottlePrice(Double bottlePrice) {
		this.bottlePrice = bottlePrice;
	}
}
