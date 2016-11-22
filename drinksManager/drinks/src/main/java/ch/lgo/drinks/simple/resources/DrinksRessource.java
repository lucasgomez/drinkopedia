package ch.lgo.drinks.simple.resources;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.exceptions.UnknownDrinkType;
import ch.lgo.drinks.simple.service.IDrinksService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Path("/drinks")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class DrinksRessource {

	@Autowired
	private IDrinksService drinksService;
	@Context
	UriInfo uriInfo;

	@GET
	@ApiOperation(value = "Find all accounts")
	@ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Have a drink", response = DrinksDTOList.class),
            @ApiResponse(code = 204, message = "No drinks to display")}) 
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

	@GET
	@Path("types/{drink_type}")
	public Response findDrinksByType(@PathParam("drink_type") String drinkTypeName) {
		DrinksDTOList drinksFound;
		try {
			drinksFound = drinksService.findDrinksByType(drinkTypeName);
		} catch (UnknownDrinkType e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		if (drinksFound != null && !drinksFound.getDrinks().isEmpty()) {
			return Response.ok().entity(drinksFound).build();
		} else {
			return Response.noContent().build();
		}
	}

	@GET
	@Path("search/{drink_name}")
	public Response findDrinksByName(@PathParam("drink_name") String drinkName) {
		DrinksDTOList drinksFound = drinksService.findDrinksByName(drinkName);
		
		if (drinksFound != null && !drinksFound.getDrinks().isEmpty()) {
			return Response.ok().entity(drinksFound).build();
		} else {
			return Response.noContent().build();
		}
	}

	@POST
	public Response createDrink(DrinkDTO newDrink) {
		DrinkDTO createdDrink = drinksService.createDrink(newDrink);

		if (createdDrink == null) {
			return Response.status(Status.BAD_REQUEST).build();
		} else {
			URI location = uriInfo.getAbsolutePathBuilder().path("{id}")
					.resolveTemplate("id", createdDrink.getId()).build();

			return Response.created(location).build();
		}
	}
	
	@PUT
	@Path("{drink_id}")
	public Response updateDrink(@PathParam("drink_id") long drinkId, DrinkDTO drinkToUpdate) {
		if (drinkToUpdate != null) {
			DrinkDTO updatedDrink = drinksService.updateDrink(drinkId, drinkToUpdate);
			if (updatedDrink != null) {
				return Response.ok().entity(updatedDrink).build();
			}
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Path("{drink_id}")
	public Response deleteDrink(@PathParam("drink_id") long drinkId) {
		drinksService.deleteDrink(drinkId);
		return Response.ok().build();
	}

}
