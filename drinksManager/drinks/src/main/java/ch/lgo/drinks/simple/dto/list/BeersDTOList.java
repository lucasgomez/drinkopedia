package ch.lgo.drinks.simple.dto.list;

import java.util.ArrayList;
import java.util.List;

public class BeersDTOList<D> {
    
    private List<D> beers;
    private String name;
    private String description;
    
    public List<D> getBeers() {
        return beers;
    }
    public void setBeers(List<D> beers) {
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
