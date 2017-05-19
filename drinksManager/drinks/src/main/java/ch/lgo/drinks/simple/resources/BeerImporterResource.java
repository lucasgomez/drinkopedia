package ch.lgo.drinks.simple.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.xlsx4j.exceptions.Xlsx4jException;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.ImportDataService;
import ch.lgo.drinks.simple.service.XlsxOutputService;
import io.swagger.annotations.Api;

@RestController
@Path("/beerimporter")
@Api
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF8"})
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

    @GET
    @Path("extractdata/")
    public Response extractData() throws Exception {
		Set<String> unreferencedStyles = importDataService.extractUnreferencedBeersStyles(IMPORT_FOLDER+"beersdetails.xlsx");
		Set<String> unreferencedColors = importDataService.extractUnreferencedBeersColors(IMPORT_FOLDER+"beersdetails.xlsx");
		
		File output = outputService.outputBeerStylesAndColors(new ArrayList<>(unreferencedStyles), new ArrayList<>(unreferencedColors), OUTPUT_FOLDER, "extractedBeerDetails");
    	
		return Response.ok(output.getAbsolutePath(), MediaType.APPLICATION_JSON)
			      .header("Content-Disposition", "attachment; filename=\"" + output.getName() + "\"" ) //optional
			      .build();
    }
    
    @GET
    @Path("extractbeers/")
    public Response extractBeers() throws Exception {
    	Map<String, List<String>> unreferencedBeersAndCode = importDataService.extractUnreferencedBeersAndCode(IMPORT_FOLDER+"commandeAmstein.xlsx");
    	
    	File output = outputService.outputBeersAndCode(unreferencedBeersAndCode, OUTPUT_FOLDER, "extractedBeers");
    	
    	return null;
    }
    
    @GET
    @Path("importcolorandstyles/")
    public Response importColorsAndStyles() throws Docx4JException, Xlsx4jException {
    	Set<BeerStyle> importedBeerStyles = importDataService.importBeerStyles(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 0);
    	Set<BeerColor> importedBeerColors = importDataService.importBeerColors(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 1);
    	return Response.ok().build();
    }
    
    @GET
    @Path("importbeers/")
    public Response importBeers() throws Docx4JException, Xlsx4jException {
    	Set<Beer> importedBeerStyles = importDataService.importBeers(IMPORT_FOLDER+"extractedBeers.xlsx", 0, 0, 1);
    	return Response.ok().build();
    }
    
    @GET
    @Path("importbeerdetails/")
    public Response extractBeerDetails() throws Exception {
    	Set<Beer> unreferencedBeersAndCode = importDataService.importBeersDetails(IMPORT_FOLDER+"beersdetails.xlsx");
    	
    	return null;
    }
	
    @GET
	@Path("importprices/")
	public Response extractPricesAndServiceType() throws Exception {
		Set<Beer> unreferencedBeersAndCode = importDataService.readAndImportPricesAndServiceType(IMPORT_FOLDER+"commandeAmstein.xlsx");
		
		return null;
	}
    
    @GET
    @Path("importsellingprices")
    public Response importSellingPrices() throws Docx4JException, Xlsx4jException {
    	importDataService.importSellingPrices(IMPORT_FOLDER+"pricesCalculation.xlsx");
    	return null;
    }
    
    @GET
    @Path("importdescriptions")
    public Response importDescriptions() throws Xlsx4jException, Docx4JException {
    	Set<Beer> beersWithDesc = importDataService.importDescriptions(IMPORT_FOLDER+"completedDescriptions.xlsx");
    	
    	return null;
    }
    
    @GET
    @Path("filterbyextid")
    public Response filterByExternalId() throws Exception {
    	List<List<String>> content = importDataService.extractRowsByExternalId(IMPORT_FOLDER+"descriptifs.xlsx");
    	outputService.outputContent(content, OUTPUT_FOLDER, "filteredDescriptions");
    	return null;
    }
    
    @GET
    @Path("importproducers")
    public Response importProducers() throws Exception {
    	importDataService.importBeerProviders(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
    	return null;
    }
    
    @GET
    @Path("addproducers")
    public Response addProducersToBeer() throws Exception {
    	importDataService.updateBeersWithProducers(IMPORT_FOLDER+"extractedBeersWithProducer.xlsx");
    	return null;
    }
    
    @GET
    @Path("checkstandardimporter")
    public Response checkStandardImporterContent() throws Docx4JException, Xlsx4jException {
    	Map<String, Set<String>> unreferencedContent = importDataService.extractUnreferencedContentFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	//TODO Change to a response instead of console...
    	for (Entry<String,Set<String>> entry : unreferencedContent.entrySet()) {
			System.out.println(" - Unreferenced "+entry.getKey());
			entry.getValue().forEach(entity -> System.out.println("    - "+entity));
		}
    	return null;
    }
    
    @GET
    @Path("importfromstandardimporter")
    public Response importStandardImporterContent() throws Docx4JException, Xlsx4jException {
    	importDataService.importUnreferencedContentFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	return null;
    }
    
    @GET
    @Path("importbeersfromstandardimporter")
    public Response importStandardImporterBeers() throws Docx4JException, Xlsx4jException {
    	importDataService.extractUnreferencedBeersFromStandardImporter(IMPORT_FOLDER+"standardImporter.xlsx");
    	return null;
    }
	
    //TODO temp method
    @GET
	@Path("importalldebug/")
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
    
    @GET
    @Path("clearservice/")
    public Response clearService() {
    	//TODO Move to a suitable place...
    	importDataService.clearService();
    	return Response.ok().build();
    }
}
