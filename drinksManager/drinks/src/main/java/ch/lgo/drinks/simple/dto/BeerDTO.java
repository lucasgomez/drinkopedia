package ch.lgo.drinks.simple.dto;

public class BeerDTO {

    private Double abv;
    private Long ibu;
    private Long srm; 
//    private Set<BeerStyleDTO> styles;
//    private Set<FermentingEnum> fermentings;
//    private Set<TagDto> tags;
    
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
    
//    public Set<BeerStyleDTO> getStyles() {
//        return styles;
//    }
//    public void setStyles(Set<BeerStyleDTO> styles) {
//        this.styles = styles;
//    }
//    
//	public Set<FermentingEnum> getFermentings() {
//		return fermentings;
//	}
//	public void setFermentings(Set<FermentingEnum> fermentings) {
//		this.fermentings = fermentings;
//	}
//
//	public Set<TagDto> getTags() {
//		return tags;
//	}
//	public void setTags(Set<TagDto> tags) {
//		this.tags = tags;
//	}
}
