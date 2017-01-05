package ch.lgo.drinks.simple.dto;

import java.util.ArrayList;
import java.util.List;

public class BeersDTOList {
    
    private List<BeerDTO> beers;
    
    public List<BeerDTO> getBeers() {
        return beers;
    }
    public void setBeers(List<BeerDTO> beers) {
        this.beers = beers;
    }
    
    public BeersDTOList() {
        this.beers = new ArrayList<>();
    }

}
