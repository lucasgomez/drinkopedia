package ch.lgo.drinks.simple.dto;

import java.util.List;

public class DetailedBeerDto extends BeerDTO {
	
	private String producerOriginName;
	private String fermenting;
    private String bitternessRank;
    private String sournessRank;
    private String sweetnessRank;
    private String hoppingRank;
    private String comment;
    private Long bottleVolumeInCl;
    private Double bottleSellingPrice;
    private Double tapPriceSmall;
    private Double tapPriceBig;
    private List<BarDTO> tapBars;
    private List<BarDTO> bottleBars;
	
	public String getProducerOriginName() {
		return producerOriginName;
	}
	public void setProducerOriginName(String producerOriginName) {
		this.producerOriginName = producerOriginName;
	}

    public String getFermenting() {
        return fermenting;
    }
    public void setFermenting(String fermenting) {
        this.fermenting = fermenting;
    }
    
    public String getBitternessRank() {
        return bitternessRank;
    }
    public void setBitternessRank(String bitternessRank) {
        this.bitternessRank = bitternessRank;
    }
    
    public String getSournessRank() {
        return sournessRank;
    }
    public void setSournessRank(String sournessRank) {
        this.sournessRank = sournessRank;
    }
    
    public String getSweetnessRank() {
        return sweetnessRank;
    }
    public void setSweetnessRank(String sweetnessRank) {
        this.sweetnessRank = sweetnessRank;
    }
    
    public String getHoppingRank() {
        return hoppingRank;
    }
    public void setHoppingRank(String hoppingRank) {
        this.hoppingRank = hoppingRank;
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

    public List<BarDTO> getTapBars() {
        return tapBars;
    }
    public void setTapBars(List<BarDTO> tapBars) {
        this.tapBars = tapBars;
    }
    public List<BarDTO> getBottleBars() {
        return bottleBars;
    }
    public void setBottleBars(List<BarDTO> bottleBars) {
        this.bottleBars = bottleBars;
    }
	
}
