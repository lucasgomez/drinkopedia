package ch.lgo.drinks.simple.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.UriInfo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path("/beers")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class BeersResource {

    @Autowired
    private BeersServiceImpl beersService;

    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    @GET
    @ApiOperation(value = "Find all beers")
    public Response getBeers() throws NoContentFoundException {
        List<Beer> beers = beersService.getAll();
        return Response.ok().entity(convertToBeersListDTO(beers)).build();
    }

    @GET
    @Path("{beer_id}")
    public Response getBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        Beer beer = beersService.loadById(beerId);
        return Response.ok().entity(convertToDto(beer)).build();
    }

    @GET
    @Path("types/{beer_style_id}")
    public Response findBeersByStyle(
            @PathParam("beer_style_id") long beerStyleId)
            throws NoContentFoundException {
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByStyleId(beerStyleId));
        return Response.ok().entity(beersFound).build();
    }

    @GET
    @Path("search/{beer_name}")
    public Response findBeersByName(@PathParam("beer_name") String beerName)
            throws NoContentFoundException {
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByName(beerName));

        return Response.ok().entity(beersFound).build();
    }

    @POST
    public Response createBeer(BeerDTO newBeer) throws BadCreationRequestException {
        Beer createdBeer = beersService.create(convertToEntity(newBeer));
        URI location = uriInfo.getAbsolutePathBuilder().path("{id}")
                .resolveTemplate("id", createdBeer.getId()).build();

        return Response.created(location).build();
    }

    @PUT
    @Path("{beer_id}")
    public Response updateBeer(@PathParam("beer_id") long beerId,
            BeerDTO beerToUpdate) throws ResourceNotFoundException, BadCreationRequestException {
        return Response.ok().entity(
                    beersService.update(beerId, convertToEntity(beerToUpdate)))
                    .build();
    }

    @DELETE
    @Path("{beer_id}")
    public Response deleteBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        beersService.delete(beerId);
        return Response.ok().build();
    }

    private BeersDTOList convertToBeersListDTO(List<Beer> beers) {
        BeersDTOList beersDTOList = new BeersDTOList();
        List<BeerDTO> beersList = beers.stream().map(beer -> convertToDto(beer))
                .collect(Collectors.toList());
        beersDTOList.setBeers(beersList);
        return beersDTOList;
    }

    private Beer convertToEntity(BeerDTO postedBeer) {
        Beer beer = modelMapper.map(postedBeer, Beer.class);
        return beer;
    }

    private BeerDTO convertToDto(Beer beer) {
        BeerDTO beerDTO = modelMapper.map(beer, BeerDTO.class);
        return beerDTO;
    }
}
