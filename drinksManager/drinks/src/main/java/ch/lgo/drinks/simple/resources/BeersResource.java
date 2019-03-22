package ch.lgo.drinks.simple.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersService;

@RestController
@CrossOrigin(origins={"http://localhost:3000"})
@RequestMapping("/private/beers")
public class BeersResource {

    @Autowired
    private BeersService beersService;
    
    @Context
    UriInfo uriInfo;

    @PostMapping
    public Response createBeer(BeerDTO newBeer) throws BadCreationRequestException {
//        Beer createdBeer = beersService.create(convertToEntity(newBeer));
//        URI location = uriInfo.getAbsolutePathBuilder().path("{id}")
//                .resolveTemplate("id", createdBeer.getId()).build();
//
//        return Response.created(location).build();
        return Response.ok().build();
    }

    @PutMapping("{beer_id}")
    public Response updateBeer(@PathVariable("beer_id") long beerId, @RequestBody DetailedBeerDto beerToUpdate) {
        try {
            Beer updatedBeer = beersService.update(beerId, beerToUpdate);
            return Response.ok()
                    .entity(updatedBeer)
                    .build();
        } catch (ResourceNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    
    @DeleteMapping("{beer_id}")
    public Response deleteBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        beersService.delete(beerId);
        return Response.ok().build();
    }
}
