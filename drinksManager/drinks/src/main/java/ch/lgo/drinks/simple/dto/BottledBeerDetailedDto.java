package ch.lgo.drinks.simple.dto;

import ch.lgo.drinks.simple.entity.FermentingEnum;

public class BottledBeerDetailedDto {
	
	private String name;
	private String producerName;
	private String producerOriginName;
	private String producerOriginShortName;
    private Double abv; //Alcool
	private String styleName;
	private String colorName;
    private FermentingEnum fermenting;
	private String comment;
	private Long bottleVolumeInCl;
	private Double bottleSellingPrice;
	
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
	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
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
	public Double getBottleSellingPrice() {
		return bottleSellingPrice;
	}
	public void setBottleSellingPrice(Double bottlePrice) {
		this.bottleSellingPrice = bottlePrice;
	}
	
	public BottledBeerDetailedDto() {}
	
}
