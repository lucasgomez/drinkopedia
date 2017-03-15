package ch.lgo.drinks.simple.dto.list;

import java.util.ArrayList;
import java.util.List;

import ch.lgo.drinks.simple.dto.BeerDTO;

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
