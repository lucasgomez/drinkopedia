package ch.lgo.drinks.simple;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//TODO Check why init data scripts are not loaded (see errors at server startup "SqlExceptionHelper   : SQL Warning Code: -1100, SQLState: 02000", "SqlExceptionHelper   : no data")
@SpringBootApplication
//TODO Understand why next annotation doesn't work (using JerseyConfig.register() instead...)
//@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Provider.class))
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
