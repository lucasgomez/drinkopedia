package ch.lgo.drinks.simple.service;

import org.springframework.beans.factory.annotation.Autowired;

import ch.lgo.drinks.simple.dao.BeerColorRepository;
import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.entity.BeerColor;

public class ColorService extends AbstractCrudService<BeerColor> {
    
    @Autowired
    BeerColorRepository colorRepository;
    
    @Override
    protected ICrudRepository<BeerColor> getCrudRepository() {
        return colorRepository;
    }

}
