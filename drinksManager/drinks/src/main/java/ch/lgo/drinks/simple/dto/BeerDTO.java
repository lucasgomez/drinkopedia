package ch.lgo.drinks.simple.dto;

import java.time.LocalDateTime;

public class BeerDTO {
    
    private Long id;
    private String name;
    private Long producerId;
    private String producerName;
    private Long producerOriginId;
    private String producerOriginName;
    private String producerOriginShortName;
    private Double abv;
    private Long colorId;
    private String colorName;
    private Long styleId;
    private String styleName;
    //Bottle fields
    private Long bottleVolumeInCl;
    private Double bottleSellingPrice;
    private String bottleAvailability;
    //Tap fields
    private Double tapPriceSmall;
    private Double tapPriceBig;
    private String tapAssortment;
    private String tapAvailability;
    private LocalDateTime tapAvailabilityDate;
    private Boolean active;
    private LocalDateTime activationDate;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getAbv() {
        return abv;
    }
    public void setAbv(Double abv) {
        this.abv = abv;
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
    
    public Long getProducerId() {
        return producerId;
    }
    public void setProducerId(Long producerId) {
        this.producerId = producerId;
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

    public Long getProducerOriginId() {
        return producerOriginId;
    }
    public void setProducerOriginId(Long producerOriginId) {
        this.producerOriginId = producerOriginId;
    }
    
    public String getColorName() {
        return colorName;
    }
    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
    
    public Long getColorId() {
        return colorId;
    }
    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }
    
    public String getStyleName() {
        return styleName;
    }
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public Long getStyleId() {
        return styleId;
    }
    public void setStyleId(Long styleId) {
        this.styleId = styleId;
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
    public void setBottleSellingPrice(Double bottleSellingPrice) {
        this.bottleSellingPrice = bottleSellingPrice;
    }

    public String getBottleAvailability() {
        return bottleAvailability;
    }
    public void setBottleAvailability(String bottleAvailability) {
        this.bottleAvailability = bottleAvailability;
    }
    
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
    
    public String getTapAssortment() {
        return tapAssortment;
    }
    public void setTapAssortment(String tapAssortment) {
        this.tapAssortment = tapAssortment;
    }
    
    public String getTapAvailability() {
        return tapAvailability;
    }
    public void setTapAvailability(String tapAvailability) {
        this.tapAvailability = tapAvailability;
    }
    
    public LocalDateTime getTapAvailabilityDate() {
        return tapAvailabilityDate;
    }
    public void setTapAvailabilityDate(LocalDateTime tapAvailabilityDate) {
        this.tapAvailabilityDate = tapAvailabilityDate;
    }
    
    public Boolean isActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getActivationDate() {
        return activationDate;
    }
    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }
    
}
