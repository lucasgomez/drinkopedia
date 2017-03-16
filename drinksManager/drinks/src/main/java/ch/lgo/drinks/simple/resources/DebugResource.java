package ch.lgo.drinks.simple.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.PlaceService;
import ch.lgo.drinks.simple.service.ProducerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path("/dhebeug")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class DebugResource {

    @Autowired
    private BeersServiceImpl beersService;
    @Autowired
    private ProducerService producerService;
    @Autowired
	private PlaceService placeService;
    
	@GET
    @ApiOperation(value = "Find all beers")
    public Response insertDummyBeersAndProviders() throws NoContentFoundException, BadCreationRequestException {
        createAndSaveBeer("Dianemayte", createAndSaveProducer("ABO", "VD"));
        createAndSaveBeer("Marz'Ale", createAndSaveProducer("FdB", "CH"));
        createAndSaveBeer("Big F ale", createAndSaveProducer("Failure Brew", "UK"));

        return Response.created(null).build();
    }
    
    protected Producer createAndSaveProducer(String name, String origin) throws BadCreationRequestException {
		Producer newProducer = new Producer();
		newProducer.setName(name);
		Place newPlace = new Place();
		newPlace.setName(origin);
		newPlace = placeService.create(newPlace);
		newProducer.setOrigin(newPlace);
		return producerService.create(newProducer);
	}
    
    private Beer createAndSaveBeer(String name, Producer producer) throws BadCreationRequestException {
        Beer newBeer = new Beer();
        newBeer.setName(name);
        newBeer.setProducer(producer);
        newBeer.setAbv(0.05);
        newBeer.setIbu(30L);
        newBeer.setSrm(20L);
        return beersService.create(newBeer);
    }
}
