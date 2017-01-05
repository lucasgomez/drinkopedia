package ch.lgo.drinks.simple.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.DrinksServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//TODO Refine authentication requirements to leave some public methods
@RestController
@Path("/drinks")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class DrinksResource {

    @Autowired
    private DrinksServiceImpl drinksService;

    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    @GET
    @ApiOperation(value = "Find all accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Have a drink", response = DrinksDTOList.class),
            @ApiResponse(code = 204, message = "No drinks to display")})
    public Response getDrinks() throws NoContentFoundException {
        List<Drink> drinks = drinksService.getAll();
        DrinksDTOList drinksList = convertToDrinksListDTO(drinks);
        return Response.ok().entity(drinksList).build();
    }

    @GET
    @Path("{drink_id}")
    public Response getDrink(@PathParam("drink_id") long drinkId)
            throws ResourceNotFoundException {
        Drink drink = drinksService.loadById(drinkId);
        return Response.ok().entity(convertToDto(drink)).build();
    }

    @GET
    @Path("types/{drink_type}")
    public Response findDrinksByType(
            @PathParam("drink_type") DrinkTypeEnum drinkType) {
        DrinksDTOList drinksFound = convertToDrinksListDTO(
                drinksService.findByType(drinkType));

        if (drinksFound != null && !drinksFound.getDrinks().isEmpty()) {
            return Response.ok().entity(drinksFound).build();
        } else {
            return Response.noContent().build();
        }
    }

    @GET
    @Path("search/{drink_name}")
    public Response findDrinksByName(
            @PathParam("drink_name") String drinkName) {
        DrinksDTOList drinksFound = convertToDrinksListDTO(
                drinksService.findByName(drinkName));

        if (drinksFound != null && !drinksFound.getDrinks().isEmpty()) {
            return Response.ok().entity(drinksFound).build();
        } else {
            return Response.noContent().build();
        }
    }

    @DELETE
    @Path("{drink_id}")
    public Response deleteDrink(@PathParam("drink_id") long drinkId)
            throws ResourceNotFoundException {
        drinksService.delete(drinkId);
        return Response.ok().build();
    }

    private DrinksDTOList convertToDrinksListDTO(List<Drink> drinks) {
        DrinksDTOList drinksDTOList = new DrinksDTOList();
        List<DrinkDTO> drinksList = drinks.stream()
                .map(drink -> convertToDto(drink)).collect(Collectors.toList());
        drinksDTOList.setDrinks(drinksList);
        return drinksDTOList;
    }

    private Drink convertToEntity(DrinkDTO postedDrink) {
        Drink drink = modelMapper.map(postedDrink, Drink.class);
        return drink;
    }

    private DrinkDTO convertToDto(Drink drink) {
        DrinkDTO drinkDTO = modelMapper.map(drink, DrinkDTO.class);
        return drinkDTO;
    }

}
