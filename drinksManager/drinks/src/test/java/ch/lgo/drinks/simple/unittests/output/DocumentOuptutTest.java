package ch.lgo.drinks.simple.unittests.output;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import ch.lgo.drinks.simple.dto.BottledBeerDetailedDto;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.service.DocxOutputService;
import ch.lgo.drinks.simple.service.XlsxOutputService;

public class DocumentOuptutTest {
	
	private static final String LOREM_LONG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tellus ipsum, sollicitudin at metus ultrices, vestibulum feugiat lectus. Integer gravida ultrices est, vel euismod dolor cursus non. Mauris varius, est sit amet vestibulum vehicula, ipsum diam pulvinar est, eget facilisis turpis ante non neque. Pellentesque scelerisque velit sit amet tincidunt euismod. Donec lobortis, augue vehicula dapibus suscipit, urna purus semper dolor, id ultricies sapien sapien vel felis. Integer fringilla iaculis sapien, id feugiat leo luctus vel. Sed eget commodo nulla. ";
	private static final String LOREM_SHORT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tellus ipsum, sollicitudin at metus ultrices";

	@Test
	public void exportPriceList() throws NoContentFoundException, BadCreationRequestException, Exception {
		XlsxOutputService service = new XlsxOutputService();
		File output = service.outputBottlesPriceLists(insertDummyBeersAndProviders(), "src/test/resources/output/", "ohm");
		Desktop.getDesktop().open(output);
	}
	
	@Test
	public void exportPriceListAsDocx() throws NoContentFoundException, BadCreationRequestException, Exception {
		DocxOutputService service = new DocxOutputService();
		File output = service.outputBottlesPriceList(insertDummyBeersAndProviders(), "src/test/resources/output/", "ohm");
		Desktop.getDesktop().open(output);
	}
	
	private List<BottledBeerDetailedDto> insertDummyBeersAndProviders() throws NoContentFoundException, BadCreationRequestException {
		ModelMapper modelMapper = new ModelMapper();
		
		Place eng = createAndSaveOrigin("Angleterre", "UK");
		Place sco = createAndSaveOrigin("Ecosse", "UK");
		Place vv = createAndSaveOrigin("Vevey", "CH");
		
		Producer robinsons = createAndSaveProducer("Robinson's Brewery", eng);
		Producer brewdog = createAndSaveProducer("Brewdog", sco);
		Producer fdb = createAndSaveProducer("FdB", vv);
		
		List<Beer> drinks = new ArrayList<>();
		drinks.add(createAndSaveSellingBeer("Trooper Red 'N' Black", LOREM_LONG, robinsons, 5.5, 24, 6.0, 33L));
		drinks.add(createAndSaveSellingBeer("Dianemayte", LOREM_SHORT, fdb, 8.0, 45, 15.5, 50L));
		drinks.add(createAndSaveSellingBeer("Black Hammer", "Rien à dire", brewdog, 7.2, 42, 6.5, 33L));
		
		List<BottledBeerDetailedDto> beersDTO = new ArrayList<>();
		drinks.forEach(beer -> beersDTO.add(modelMapper.map(beer, BottledBeerDetailedDto.class)));
        return beersDTO;
    }
    
	private Place createAndSaveOrigin(String name, String shortname) throws BadCreationRequestException {
		Place newPlace = new Place();
		newPlace.setName(name);
		newPlace.setShortName(shortname);
		return newPlace;
	}
	
    private Producer createAndSaveProducer(String name, Place origin) throws BadCreationRequestException {
		Producer newProducer = new Producer();
		newProducer.setName(name);
		newProducer.setOrigin(origin);
		return newProducer;
	}
    
    private Beer createAndSaveSellingBeer(String name, String comment, Producer producer, Double abv, Integer ibu, Double price, Long volume) throws BadCreationRequestException {
        Beer newBeer = new Beer();
        newBeer.setName(name);
        newBeer.setComment(comment);
        newBeer.setProducer(producer);
        newBeer.setAbv(abv);
        newBeer.setIbu(30L);
        
        BottledBeer sellableDrink = new BottledBeer();
        sellableDrink.setBeer(newBeer);
        sellableDrink.setPrice(price);
        sellableDrink.setVolumeInCl(volume);
        
        newBeer.setBottle(sellableDrink);
        return newBeer;
    }
}
