package ch.lgo.drinks.simple.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.service.IDrinksService;

//@RestController
@Path("/drinks")
@Produces({ MediaType.APPLICATION_JSON + "; charset=UTF8" })
public class DrinksRessource {

	@Autowired
	private IDrinksService drinksService;
	@Context 
	UriInfo uriInfo;

	@GET
	public Response getDrinks() {
		DrinksDTOList drinks = drinksService.getAll();
		
		if (drinks.getDrinks().isEmpty()) {
	      return Response.status(Status.NO_CONTENT).build();
	    } else {
	      return Response.ok().entity(drinks).build();
	    }
	}
	
	@GET
	@Path("{drink_id}")
	public Response getDrink(@PathParam("drink_id") long drinkId) {
		DrinkDTO drink = drinksService.loadById(drinkId);

		if (drink == null) {
			return Response.status(Status.NOT_FOUND).build();
	    } else {
	      return Response.ok().entity(drink).build();
	    }
	}
	
	@POST
	public Response createDrink(DrinkDTO newDrink) {
		DrinkDTO createdDrink = drinksService.createDrink(newDrink);
		
		if (createdDrink == null) {
			return Response.status(Status.BAD_REQUEST).build();
		} else {
			URI location = uriInfo.getAbsolutePathBuilder()
		            .path("{id}")
		            .resolveTemplate("id", createdDrink.getId())
		            .build();
			
			return Response.created(location).build();
		}
	}
	
}
