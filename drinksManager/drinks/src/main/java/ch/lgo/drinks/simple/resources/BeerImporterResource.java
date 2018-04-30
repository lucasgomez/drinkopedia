package ch.lgo.drinks.simple.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xlsx4j.exceptions.Xlsx4jException;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.ImportDataService;
import ch.lgo.drinks.simple.service.XlsxOutputService;

@RestController
public class BeerImporterResource {

    private static final String IMPORT_FOLDER = "src/main/resources/input/";
	private static final String OUTPUT_FOLDER = "src/main/resources/output/";
	@Autowired
    private BeersServiceImpl beersService;
    @Autowired
    private ImportDataService importDataService;
    @Autowired
    private XlsxOutputService outputService;

    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    /**
     * 0nd step : Clear TapBeer and BottleBeer
     * @return
     */
    @GetMapping("/beerimporter/clearservice/")
    public Response clearService() {
        //TODO Move to a suitable place...
        importDataService.clearService();
        return Response.ok().build();
    }
    
    /**
     * 1st step : Extract unreferenced beers from order file (commandeAmstein.xlsx) and output result in a new file (extractedBeers.xlsx)
     * @return
     * @throws Exception
     */
    @GetMapping("/importer/extractbeers/")
    public Response extractBeers() throws Exception {
        Map<String, List<String>> unreferencedBeersAndCode = importDataService.extractUnreferencedBeersAndCode(IMPORT_FOLDER+"commandeAmstein.xlsx");
        
        File output = outputService.outputBeersAndCode(unreferencedBeersAndCode, OUTPUT_FOLDER, "extractedBeers");
        
        return null;
    }
    
    /**
     * 2nd step : Retrieve unreferenced beers and persist them
     * @return
     * @throws Docx4JException
     * @throws Xlsx4jException
     */
    @GetMapping("/importer/importbeers/")
    public Response importBeers() throws Docx4JException, Xlsx4jException {
        Set<Beer> importedBeerStyles = importDataService.importBeers(IMPORT_FOLDER+"extractedBeers.xlsx", 0, 0, 1);
        return Response.ok().build();
    }
    
    /**
     * 3rd step : Extract the prices and service type to import them as TapBeer and BottleBeer
     * @return
     * @throws Exception
     */
    @GetMapping("/importer/importprices/")
    public Response extractPricesAndServiceType() throws Exception {
        Set<Beer> unreferencedBeersAndCode = importDataService.readAndImportPricesAndServiceType(IMPORT_FOLDER+"commandeAmstein.xlsx");
        
        return null;
    }

    @GetMapping("/importer/extractdata/")
    public Response extractData() throws Exception {
		Set<String> unreferencedStyles = importDataService.extractUnreferencedBeersStyles(IMPORT_FOLDER+"beersdetails.xlsx");
		Set<String> unreferencedColors = importDataService.extractUnreferencedBeersColors(IMPORT_FOLDER+"beersdetails.xlsx");
		
		File output = outputService.outputBeerStylesAndColors(new ArrayList<>(unreferencedStyles), new ArrayList<>(unreferencedColors), OUTPUT_FOLDER, "extractedBeerDetails");
    	
		return Response.ok(output.getAbsolutePath(), MediaType.APPLICATION_JSON)
			      .header("Content-Disposition", "attachment; filename=\"" + output.getName() + "\"" ) //optional
			      .build();
    }
    
    @GetMapping("/importer/importcolorandstyles/")
    public Response importColorsAndStyles() throws Docx4JException, Xlsx4jException {
    	Set<BeerStyle> importedBeerStyles = importDataService.importBeerStyles(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 0);
    	Set<BeerColor> importedBeerColors = importDataService.importBeerColors(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 1);
    	return Response.ok().build();
    }
    
    @GetMapping("/importer/importbeerdetails/")
    public Response extractBeerDetails() throws Exception {
    	Set<Beer> unreferencedBeersAndCode = importDataService.importBeersDetails(IMPORT_FOLDER+"beersdetails.xlsx");
    	
    	return null;
    }
    
    @GetMapping("/importer/importsellingprices")
    public Response importSellingPrices() throws Docx4JException, Xlsx4jException {
    	importDataService.importSellingPrices(IMPORT_FOLDER+"pricesCalculation.xlsx");
    	return null;
    }
    
    @GetMapping("/importer/importdescriptions")
    public Response importDescriptions() throws Xlsx4jException, Docx4JException {
    	Set<Beer> beersWithDesc = importDataService.importDescriptions(IMPORT_FOLDER+"completedDescriptions.xlsx");
    	
    	return null;
    }
    
    @GetMapping("/importer/filterbyextid")
    public Response filterByExternalId() throws Exception {
    	List<List<String>> content = importDataService.extractRowsByExternalId(IMPORT_FOLDER+"descriptifs.xlsx");
    	outputService.outputContent(content, OUTPUT_FOLDER, "filteredDescriptions");
    	return null;
    }
    
    @GetMapping("/importer/importproducers")
    public Response importProducers() throws Exception {
    	importDataService.importBeerProviders(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
    	return null;
    }
    
    @GetMapping("/importer/importbarsselection")
    public Response importBarsSelection() throws Xlsx4jException, Docx4JException {
    	importDataService.importBarsSelection(IMPORT_FOLDER+"barsSelection.xlsx");
    	return null;
    }
    
    @GetMapping("/importer/addproducers")
    public Response addProducersToBeer() throws Exception {
    	importDataService.updateBeersWithProducers(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
    	return null;
    }
    
    @GetMapping("/importer/checkstandardimporter")
    public Response checkStandardImporterContent() throws Docx4JException, Xlsx4jException {
    	Map<String, Set<String>> unreferencedContent = importDataService.extractUnreferencedContentFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	//TODO Change to a response instead of console...
    	for (Entry<String,Set<String>> entry : unreferencedContent.entrySet()) {
			System.out.println(" - Unreferenced "+entry.getKey());
			entry.getValue().forEach(entity -> System.out.println("    - "+entity));
		}
    	return null;
    }
    
    @GetMapping("/importer/importfromstandardimporter")
    public Response importStandardImporterContent() throws Docx4JException, Xlsx4jException {
    	importDataService.importUnreferencedContentFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	return null;
    }
    
    /**
     * Import data to update from the fullMonty export, but only the Beer tab
     * @return
     */
    @GetMapping("/importer/importdatamodifier")
    public Response importDataModifier() throws Docx4JException, Xlsx4jException {
        importDataService.updateDataFromFullMonty(IMPORT_FOLDER+"fullMontyImporter.xlsx");
        return null;
    }
    
    @GetMapping("/importer/importbeersfromstandardimporter")
    public Response importStandardImporterBeers() throws Docx4JException, Xlsx4jException {
    	importDataService.extractUnreferencedBeersFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	return null;
    }
	
    //TODO temp method
    @GetMapping("/importer/importalldebug/")
	public Response extractAll() throws Exception {
    	importDataService.importBeerStyles(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 0);
    	importDataService.importBeerColors(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 1);
    	importDataService.importBeers(IMPORT_FOLDER+"extractedBeers.xlsx", 0, 0, 1);
    	importDataService.importBeersDetails(IMPORT_FOLDER+"beersdetails.xlsx");
    	importDataService.importBeerProviders(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
    	importDataService.updateBeersWithProducers(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
		importDataService.readAndImportPricesAndServiceType(IMPORT_FOLDER+"commandeAmstein.xlsx");
//		importDataService.importUnreferencedContentFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	importDataService.importSellingPrices(IMPORT_FOLDER+"pricesCalculation.xlsx");
    	importDataService.importDescriptions(IMPORT_FOLDER+"completedDescriptions.xlsx");
		
		return null;
	}
    
}
