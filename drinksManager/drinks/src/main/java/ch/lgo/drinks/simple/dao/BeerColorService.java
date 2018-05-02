package ch.lgo.drinks.simple.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.service.AbstractCrudService;

@Service
public class BeerColorService extends AbstractCrudService<BeerColor> {

    @Autowired
    BeerColorRepository colorRepository;
    
    @Override
    protected ICrudRepository<BeerColor> getCrudRepository() {
        return colorRepository;
    }

}
