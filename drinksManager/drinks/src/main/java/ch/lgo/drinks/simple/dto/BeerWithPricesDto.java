package ch.lgo.drinks.simple.dto;

public class BeerWithPricesDto extends BeerDTO {
    
    private Double tapBuyingPricePerLiter;
    private Double bottleBuyingPrice;
    
    public Double getTapBuyingPricePerLiter() {
        return tapBuyingPricePerLiter;
    }
    public void setTapBuyingPricePerLiter(Double buyingPricePerLiter) {
        this.tapBuyingPricePerLiter = buyingPricePerLiter;
    }

    public Double getBottleBuyingPrice() {
        return bottleBuyingPrice;
    }
    public void setBottleBuyingPrice(Double buyingPrice) {
        this.bottleBuyingPrice = buyingPrice;
    }
}
