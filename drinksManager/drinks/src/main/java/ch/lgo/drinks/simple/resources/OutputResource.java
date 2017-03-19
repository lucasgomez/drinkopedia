package ch.lgo.drinks.simple.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.OdtOutputService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path("/output")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class OutputResource {

	@Autowired
	private OdtOutputService outputService;
	@Autowired
	private BeersServiceImpl beersService;
	@Autowired
	private ModelMapper modelMapper;
	
	@GET
    @ApiOperation(value = "Find all beers")
    public Response createBeerOutput() throws Exception {
		List<Beer> beers = beersService.getAll();
		List<BeerDTO> beersList = new ArrayList<>();
		beers.forEach(beer -> beersList.add(modelMapper.map(beer, BeerDTO.class)));
		outputService.outputTapBeers(beersList, "mu.odt");
        return Response.created(null).build();
    }
	
}
