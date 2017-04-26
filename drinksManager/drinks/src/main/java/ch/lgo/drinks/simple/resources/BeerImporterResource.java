package ch.lgo.drinks.simple.resources;

import java.io.File;
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

    private static final String IMPORT_FOLDER = "src/test/resources/input/";
	private static final String OUTPUT_FOLDER = "src/test/resources/output/";
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
		
		File output = outputService.outputBeerStylesAndColors(unreferencedStyles, unreferencedColors, OUTPUT_FOLDER, "extractedBeerDetails");
    	
		return Response.ok(output.getAbsolutePath(), MediaType.APPLICATION_JSON)
			      .header("Content-Disposition", "attachment; filename=\"" + output.getName() + "\"" ) //optional
			      .build();
    }
    
    
    @GET
    @Path("importdata/")
    public Response importData() throws Docx4JException, Xlsx4jException {
    	Set<BeerStyle> importedBeerStyles = importDataService.importBeerStyles(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 0);
    	Set<BeerColor> importedBeerColors = importDataService.importBeerColors(IMPORT_FOLDER+"extractedBeerDetails.xlsx", 1);
    	return Response.ok().build();
    }
}
