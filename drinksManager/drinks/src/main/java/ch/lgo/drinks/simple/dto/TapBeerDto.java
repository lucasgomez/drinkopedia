package ch.lgo.drinks.simple.dto;

import java.util.Set;

import ch.lgo.drinks.simple.entity.HasBarsId;

public class TapBeerDto implements HasBarsId {

    private Long beerId;

    private Double priceSmall;
    private Double priceBig;
    private Double buyingPricePerLiter;
    
    private String assortment;
    private String availability;
    
    private Set<Long> barsIds;

    public Long getBeerId() {
        return beerId;
    }
    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public Double getPriceSmall() {
        return priceSmall;
    }
    public void setPriceSmall(Double priceSmall) {
        this.priceSmall = priceSmall;
    }

    public Double getPriceBig() {
        return priceBig;
    }
    public void setPriceBig(Double priceBig) {
        this.priceBig = priceBig;
    }

    public Double getBuyingPricePerLiter() {
        return buyingPricePerLiter;
    }
    public void setBuyingPricePerLiter(Double buyingPricePerLiter) {
        this.buyingPricePerLiter = buyingPricePerLiter;
    }

    public String getAssortment() {
        return assortment;
    }
    public void setAssortment(String assortment) {
        this.assortment = assortment;
    }

    public String getAvailability() {
        return availability;
    }
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Set<Long> getBarsIds() {
        return barsIds;
    }
    public void setBarsIds(Set<Long> barsId) {
        this.barsIds = barsId;
    }
    
    
}
