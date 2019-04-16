package ch.lgo.drinks.simple.dto;

import java.util.Set;

public class VeryDetailedBeerDto extends DetailedBeerDto {

    private Double tapBuyingPricePerLiter;
    private Double bottleBuyingPrice;
    private Set<Long> tapBarsIds;
    private Set<Long> bottleBarsIds;

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
    
    public Set<Long> getTapBarsIds() {
        return tapBarsIds;
    }
    public void setTapBarsIds(Set<Long> tapBarsId) {
        this.tapBarsIds = tapBarsId;
    }
    
    public Set<Long> getBottleBarsIds() {
        return bottleBarsIds;
    }
    public void setBottleBarsIds(Set<Long> bottleBarsIds) {
        this.bottleBarsIds = bottleBarsIds;
    }
    
}
