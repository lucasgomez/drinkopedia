package ch.lgo.drinks.simple;

import javax.annotation.PostConstruct;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.lgo.drinks.simple.resources.BeersResource;
import ch.lgo.drinks.simple.resources.BusinessExceptionMapper;
import ch.lgo.drinks.simple.resources.DrinksResource;
import ch.lgo.drinks.simple.resources.GenericExceptionMapper;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@Component
public class JerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path:/}")
    private String apiPath;
    
    public JerseyConfig() {
        registerEndpoints();
    }

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        configureSwagger();
    }

    private void registerEndpoints() {
        register(DrinksResource.class);
        register(BeersResource.class);
        register(BusinessExceptionMapper.class);
        register(GenericExceptionMapper.class);
    }

    private void configureSwagger() {
        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("drinkopedia-jersey-swagger-config");
        config.setTitle("Drinkopedia - Jersey doc");
        config.setVersion("v1");
        config.setContact("LGO");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(apiPath);
        config.setResourcePackage("ch.lgo.drinks.simple.resources");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
    
}