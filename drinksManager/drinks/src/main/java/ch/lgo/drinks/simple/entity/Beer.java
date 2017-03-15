package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
public class Beer extends Drink {

    private Double abv; //Alcool
    private Long ibu; //Bitterness
    private Long srm; //Color 
    private Set<BeerStyle> styles = new HashSet<>();
    private Set<FermentingEnum> fermentings = new HashSet<>();
	private Set<Tag> tags = new HashSet<>(); 
    
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
    
    @ManyToMany
    public Set<BeerStyle> getStyles() {
        return styles;
    }
    public void setStyles(Set<BeerStyle> styles) {
        this.styles = styles;
    }

	@ElementCollection
    public Set<FermentingEnum> getFermentings() {
		return fermentings;
	}
	public void setFermentings(Set<FermentingEnum> fermentings) {
		this.fermentings = fermentings;
	}

    @ManyToMany
	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

    @Override
    @Transient
    public DrinkTypeEnum getDrinkType() {
        return DrinkTypeEnum.BEER;
    }

    public Beer() {}
    
}
