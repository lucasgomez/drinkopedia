package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class Beer extends Drink {

    private Double abv;
    private Long ibu;
    private Long srm; 
//    private Set<BeerStyle> styles;
    
    public Double getAbv() {
        return abv;
    }
    public void setAbv(Double abv) {
        this.abv = abv;
    }
    
    public Long getIbu() {
        return ibu;
    }
    public void setIbu(Long ibu) {
        this.ibu = ibu;
    }
    
    public Long getSrm() {
        return srm;
    }
    public void setSrm(Long srm) {
        this.srm = srm;
    }
    
//    public Set<BeerStyle> getStyles() {
//        return styles;
//    }
//    public void setStyles(Set<BeerStyle> styles) {
//        this.styles = styles;
//    }
    
    @Override
    @Transient
    public DrinkTypeEnum getDrinkType() {
        return DrinkTypeEnum.BEER;
    }

    public Beer() {}
    
}
