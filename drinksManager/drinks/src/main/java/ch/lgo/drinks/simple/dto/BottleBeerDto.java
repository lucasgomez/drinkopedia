package ch.lgo.drinks.simple.dto;

import java.util.Set;

import ch.lgo.drinks.simple.entity.HasBarsId;

public class BottleBeerDto implements HasBarsId {

    private Long beerId;
    
    private Long volumeInCl;
    private Double sellingPrice;
    private Double buyingPrice;
    
    private String availability;
    private Set<Long> barsIds;
    
    public Long getBeerId() {
        return beerId;
    }
    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }
    
    public Long getVolumeInCl() {
        return volumeInCl;
    }
    public void setVolumeInCl(Long volumeInCl) {
        this.volumeInCl = volumeInCl;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    
    public Double getBuyingPrice() {
        return buyingPrice;
    }
    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
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
    public void setBarsIds(Set<Long> barsIds) {
        this.barsIds = barsIds;
    }
}
