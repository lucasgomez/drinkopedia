package ch.lgo.drinks.simple;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import ch.lgo.drinks.simple.resources.BeersResource;
import ch.lgo.drinks.simple.resources.BusinessExceptionMapper;
import ch.lgo.drinks.simple.resources.DrinksResource;
import ch.lgo.drinks.simple.resources.GenericExceptionMapper;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(DrinksResource.class);
        register(BeersResource.class);
        register(BusinessExceptionMapper.class);
        register(GenericExceptionMapper.class);
    }

}