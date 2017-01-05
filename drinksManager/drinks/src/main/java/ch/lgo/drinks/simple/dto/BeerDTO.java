package ch.lgo.drinks.simple.dto;

import java.util.Set;

public class BeerDTO extends DrinkDTO {

    private Double abv;
    private Long ibu;
    private Long srm; 
    private Set<BeerStyleDTO> styles;
    
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
    
    public Set<BeerStyleDTO> getStyles() {
        return styles;
    }
    public void setStyles(Set<BeerStyleDTO> styles) {
        this.styles = styles;
    }
}
