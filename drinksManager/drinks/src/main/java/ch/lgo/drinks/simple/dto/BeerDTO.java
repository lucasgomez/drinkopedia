package ch.lgo.drinks.simple.dto;

public class BeerDTO {
    
    private Long id;
    private String name;
    private Long producerId;
    private String producerName;
    private Long producerOriginId;
    private String producerOriginShortName;
    private Double abv;
    private Long colorId;
    private String colorName;
    private Long styleId;
    private String styleName;
    private Long bottleVolumeInCl;
    private Double bottleSellingPrice;
    private Double tapPriceSmall;
    private Double tapPriceBig;
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
