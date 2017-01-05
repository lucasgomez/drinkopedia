package ch.lgo.drinks.simple.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.DrinksServiceImpl;

@RestController
@Path("/drinksdata")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class DrinksDataResources {

    @Autowired
    private BeersServiceImpl beersService;
    @Autowired
    private DrinksServiceImpl drinksService;

    @Autowired
    ModelMapper modelMapper;
    
    @GET
    @Path("{drink_id}")
    public Response getDrinkWithData(@PathParam("drink_id") long drinkId) throws ResourceNotFoundException {
        Drink drink = drinksService.loadById(drinkId);
        
        switch (drink.getDrinkType()) {
            case BEER :
                Beer beer = beersService.loadById(drinkId);
                return Response.ok(convertToDto(beer)).build();
            default :
                //TODO Complete with other drink types
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private BeerDTO convertToDto(Beer beer) {
        BeerDTO beerDTO = modelMapper.map(beer, BeerDTO.class);
        return beerDTO;
    }
}
