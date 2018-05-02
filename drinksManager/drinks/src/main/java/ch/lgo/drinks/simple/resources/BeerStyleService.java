package ch.lgo.drinks.simple.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BeerStylesRepository;
import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.service.AbstractCrudService;

@Service
public class BeerStyleService extends AbstractCrudService<BeerStyle> {

    @Autowired
    BeerStylesRepository stylesRepository;
    
    @Override
    protected ICrudRepository<BeerStyle> getCrudRepository() {
        return stylesRepository;
    }

}
