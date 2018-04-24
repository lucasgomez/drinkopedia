package ch.lgo.drinks.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.BarRepository;
import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.entity.Bar;

@Service
public class BarService extends AbstractCrudService<Bar> {

    @Autowired
    BarRepository barRepository;
    
    @Override
    protected ICrudRepository<Bar> getCrudRepository() {
        return barRepository;
    }}
