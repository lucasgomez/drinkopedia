package ch.lgo.drinks.simple;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import ch.lgo.drinks.simple.resources.ResourceNotFoundExceptionMapper;
import ch.lgo.drinks.simple.resources.DrinksRessource;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(DrinksRessource.class);
        register(ResourceNotFoundExceptionMapper.class);
    }

}