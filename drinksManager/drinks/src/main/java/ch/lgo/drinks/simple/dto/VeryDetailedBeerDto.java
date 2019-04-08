package ch.lgo.drinks.simple.dto;

public class VeryDetailedBeerDto extends DetailedBeerDto {

    private Double tapBuyingPricePerLiter;
    private Double bottleBuyingPrice;

    public Double getTapBuyingPricePerLiter() {
        return tapBuyingPricePerLiter;
    }
    public void setTapBuyingPricePerLiter(Double tapBuyingPricePerLiter) {
        this.tapBuyingPricePerLiter = tapBuyingPricePerLiter;
    }
    
    public Double getBottleBuyingPrice() {
        return bottleBuyingPrice;
    }
    public void setBottleBuyingPrice(Double bottleBuyingPrice) {
        this.bottleBuyingPrice = bottleBuyingPrice;
    }
    
}
