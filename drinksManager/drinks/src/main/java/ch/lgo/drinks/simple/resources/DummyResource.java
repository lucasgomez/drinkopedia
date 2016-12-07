package ch.lgo.drinks.simple.resources;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@RestController
@RequestMapping({"","/"})  
public class DummyResource {
    
    @RequestMapping("/ex1")  
    public String ex1() throws ResourceNotFoundException{  
        // will be catched by global exception handler method handleBaseException  
        throw new ResourceNotFoundException("Foke");  
    }  
    
    @ExceptionHandler(value = ResourceNotFoundException.class)  
    public String nfeHandler(ResourceNotFoundException e){  
        return e.getMessage();  
    }  
}
