package ch.lgo.drinks.simple.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
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

    @Autowired
    ModelMapper modelMapper;

    @GET
    @ApiOperation(value = "Find all accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Have a drink", response = DrinksDTOList.class),
            @ApiResponse(code = 204, message = "No drinks to display")})
    public Response getDrinks() {
        List<Drink> drinks = drinksService.getAll();

        if (drinks.isEmpty()) {
            return Response.status(Status.NO_CONTENT).build();
        } else {
            DrinksDTOList drinksList = convertToDrinksListDTO(drinks);
            return Response.ok().entity(drinksList).build();
        }
    }

    @GET
    @Path("{drink_id}")
    public Response getDrink(@PathParam("drink_id") long drinkId) throws ResourceNotFoundException {
        Drink drink = drinksService.loadById(drinkId);
        if (drink == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok().entity(convertToDto(drink)).build();
        }
    }

    @GET
    @Path("types/{drink_type}")
    public Response findDrinksByType(
            @PathParam("drink_type") String drinkTypeName) {
        DrinksDTOList drinksFound;
        try {
            drinksFound = convertToDrinksListDTO(
                    drinksService.findDrinksByType(drinkTypeName));
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
    public Response findDrinksByName(
            @PathParam("drink_name") String drinkName) {
        DrinksDTOList drinksFound = convertToDrinksListDTO(
                drinksService.findDrinksByName(drinkName));

        if (drinksFound != null && !drinksFound.getDrinks().isEmpty()) {
            return Response.ok().entity(drinksFound).build();
        } else {
            return Response.noContent().build();
        }
    }

    @POST
    public Response createDrink(DrinkDTO newDrink) throws ResourceNotFoundException {
        Drink createdDrink;
        createdDrink = drinksService.createDrink(convertToEntity(newDrink));

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
    public Response updateDrink(@PathParam("drink_id") long drinkId,
            DrinkDTO drinkToUpdate) throws ResourceNotFoundException {
        if (drinkToUpdate != null) {
            return Response.ok().entity(drinksService.updateDrink(drinkId,
                    convertToEntity(drinkToUpdate))).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("{drink_id}")
    public Response deleteDrink(@PathParam("drink_id") long drinkId) throws ResourceNotFoundException {
        drinksService.deleteDrink(drinkId);
        return Response.ok().build();
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public Response handleResourceNotFound(NumberFormatException ex) {
        return Response.status(Status.NOT_FOUND).build();
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
