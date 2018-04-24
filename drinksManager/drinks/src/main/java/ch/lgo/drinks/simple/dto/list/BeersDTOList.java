package ch.lgo.drinks.simple.dto.list;

import java.util.ArrayList;
import java.util.List;

import ch.lgo.drinks.simple.dto.BeerDTO;

public class BeersDTOList {
    
    private List<BeerDTO> beers;
    private String name;
    private String description;
    
    public List<BeerDTO> getBeers() {
        return beers;
    }
    public void setBeers(List<BeerDTO> beers) {
        this.beers = beers;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BeersDTOList() {
        this.beers = new ArrayList<>();
    }

}
