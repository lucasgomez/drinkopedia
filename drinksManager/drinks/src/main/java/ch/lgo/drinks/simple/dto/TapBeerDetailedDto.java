package ch.lgo.drinks.simple.dto;

public class TapBeerDetailedDto extends DetailedBeerDto {

	private Double tapPriceSmall;
	private Double tapPriceBig;
	
	public Double getTapPriceSmall() {
		return tapPriceSmall;
	}
	public void setTapPriceSmall(Double tapPriceSmall) {
		this.tapPriceSmall = tapPriceSmall;
	}
	
	public Double getTapPriceBig() {
		return tapPriceBig;
	}
	public void setTapPriceBig(Double tapPriceBig) {
		this.tapPriceBig = tapPriceBig;
	}
}
