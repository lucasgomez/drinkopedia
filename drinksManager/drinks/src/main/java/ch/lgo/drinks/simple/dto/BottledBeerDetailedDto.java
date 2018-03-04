package ch.lgo.drinks.simple.dto;

public class BottledBeerDetailedDto extends DetailedBeerDto {
	
	private Long bottleVolumeInCl;
	private Double bottleSellingPrice;

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
