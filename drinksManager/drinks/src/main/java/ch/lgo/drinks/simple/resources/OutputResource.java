package ch.lgo.drinks.simple.resources;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dao.BarRepository;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.XlsxOutputService;
import io.swagger.annotations.Api;

@RestController
@Path("/output")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class OutputResource {
	//TODO Correct service VS dao usage
	private static final String OUTPUT_FOLDER = "src/main/resources/output/";
	@Autowired
	private XlsxOutputService outputService;
	@Autowired
	private BeersServiceImpl beersService;
	@Autowired
	private BarRepository barRepo;
	
	@GET
    @Path("bottledBar/{bottled_bar_id}")
    public Response outputBottledBar(@PathParam("bottled_bar_id") long bottledBarId) throws Exception {
		Collection<Bar> findAll = barRepo.findAll();
		Bar bottledBar = barRepo.loadBottledById(bottledBarId);
		if (bottledBar == null)
			throw new ResourceNotFoundException("No bar with id "+bottledBarId);
		
		outputService.outputBottledPriceLists(bottledBar, OUTPUT_FOLDER, bottledBarId+" - "+bottledBar.getName());
        return Response.created(null).build();
    }
	
	@GET
	@Path("tapBar/{tap_bar_id}")
	public Response outputTapBar(@PathParam("tap_bar_id") long tapBarId) throws ResourceNotFoundException {
		Bar tapBar = barRepo.loadTapById(tapBarId);
		if (tapBar == null)
			throw new ResourceNotFoundException("No bar with id "+tapBarId);
		
		outputService.outputTapBar(tapBar, OUTPUT_FOLDER, tapBarId+" - "+tapBar.getName());
		return null;
	}
	
	@GET
	@Path("pricesdefinition")
	public Response createBeersPriceDefinition() throws Exception {
		List<Beer> beers = beersService.getAllWithService();
		File file = outputService.outputBeersPricesWithDetails(beers, OUTPUT_FOLDER, "pricesCalculation");
		return null;
	}
	
	@GET
	@Path("fullmonty")
	public Response fullMonty() throws Exception {
		outputService.theFullMonty(beersService.getAllWithService(), OUTPUT_FOLDER, "fullMonty");
		return null;
	}
	
	@GET
	@Path("getbarsimporter")
	public Response getBarsImporter() throws Exception {
		outputService.outputBeerByBarsImporter(beersService.getAllWithService(), barRepo.findAll(), OUTPUT_FOLDER, "barsSelection");
		return null;
	}
	
}
