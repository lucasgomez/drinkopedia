package ch.lgo.drinks.simple.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.service.IDrinksService;

//@RestController
@Path("/drinks")
@Produces({ MediaType.APPLICATION_JSON + "; charset=UTF8" })
public class DrinksRessource {

	@Autowired
	private IDrinksService drinksService;

	@GET
	public Response getDrinks() {
		DrinksDTOList drinks = drinksService.getAll();

		if (drinks.getDrinks().isEmpty()) {
	      return Response.status(Status.NO_CONTENT).build();
	    } else {
	      return Response.ok().entity(drinks).build();
	    }
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public @ResponseBody DrinkDTO createDrink(@RequestParam(value="name", required=true) String name) {
		DrinkDTO newDrink = drinksService.createDrink(name);
		return newDrink;
	}
	
}
