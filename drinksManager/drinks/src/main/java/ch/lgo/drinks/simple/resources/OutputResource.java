package ch.lgo.drinks.simple.resources;

import java.io.File;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dao.BarRepository;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersService;
import ch.lgo.drinks.simple.service.XlsxOutputService;

@RestController
@RequestMapping
public class OutputResource {
	//TODO Correct service VS dao usage
	private static final String OUTPUT_FOLDER = "src/main/resources/output/";
	@Autowired
	private XlsxOutputService outputService;
	@Autowired
	private BeersService beersService;
	@Autowired
	private BarRepository barRepo;
	
	@GetMapping("output/bottledBar/{bottled_bar_id}")
    public Response outputBottledBar(@PathVariable(value="bottled_bar_id") long bottledBarId) throws Exception {
		Bar bottledBar = barRepo.loadBottledById(bottledBarId);
		if (bottledBar == null)
			throw new ResourceNotFoundException();
		
		outputService.outputBottledBarPricesLists(bottledBar, OUTPUT_FOLDER, bottledBar.getName());
        return Response.created(null).build();
    }
	
	@GetMapping("output/tapBar/{tap_bar_id}")
	public Response outputTapBar(@PathVariable(value="tap_bar_id") long tapBarId) throws Exception {
		Bar tapBar = barRepo.loadTapById(tapBarId);
		if (tapBar == null)
			throw new ResourceNotFoundException();
		
		outputService.outputTapBarPricesLists(tapBar, OUTPUT_FOLDER, tapBar.getName());
		return null;
	}
	
	@GetMapping("output/listWithBottlesDetails/{bottled_bar_id}")
	public Response listBottlesWithDetails(@PathParam("bottled_bar_id") long bottledBarId) throws Exception {
		Bar bottledBar = barRepo.loadBottledById(bottledBarId);
		if (bottledBar == null)
			throw new ResourceNotFoundException();
		outputService.outputBottledDetailedMultiSortedAlphaPlusPlus(bottledBar, OUTPUT_FOLDER, "details");
		return null;
	}
	
	/**
	 * @return a list of all beers with buying price and excel formulas for price calculation and later import
	 * @throws Exception
	 */
	@GetMapping("output/pricesdefinition")
	public Response createBeersPriceDefinition() throws Exception {
		List<Beer> beers = beersService.getAllWithService();
		File file = outputService.outputBeersPricesWithDetails(beers, OUTPUT_FOLDER, "pricesCalculation");
		return null;
	}
	
	/**
	 * To output all data. As a backup or for dataModifier {@code BeerImporterResource#importDataModifier()}
	 * @return
	 * @throws Exception
	 */
	@GetMapping("output/fullmonty")
	public Response fullMonty() throws Exception {
	    //TODO Change behaviour to export all and not only stuff related to beer (an unreferenced producer will be ignored)
		outputService.theFullMonty(beersService.getAllWithService(), OUTPUT_FOLDER, "fullMonty");
		return null;
	}
	
	/**
	 * Output the importer file for bars selection with all beers and bars. Later used by /importer/importbarsselection
	 * @return
	 * @throws Exception
	 */
	@GetMapping("output/getbarsimporter")
	public Response getBarsImporter() throws Exception {
		outputService.outputBeerByBarsImporter(beersService.getAllWithService(), barRepo.findAll(), OUTPUT_FOLDER, "barsSelection");
		return null;
	}
}
