package ch.lgo.drinks.simple.dto;

import java.time.LocalDateTime;

public class BeerDataForEditDto {
    private Long id;
    private String name;
    private String comment;
    private Double abv;
    private Long styleId;
    private Long colorId;
    private Long producerId;

    private String bitternessRank;
    private String sournessRank;
    private String sweetnessRank;
    private String hoppingRank;
    
    private Boolean active;
    private LocalDateTime activationDate;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getAbv() {
        return abv;
    }
    public void setAbv(Double abv) {
        this.abv = abv;
    }
    
    public Long getProducerId() {
        return producerId;
    }
    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }
    
    public Long getColorId() {
        return colorId;
    }
    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public Long getStyleId() {
        return styleId;
    }
    public void setStyleId(Long styleId) {
        this.styleId = styleId;
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
