package ch.lgo.drinks.simple.unittests.output;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.service.OdsOutputService;

public class DocumentOuptutTest {
	
	@Test
	public void exportPriceListOds() throws NoContentFoundException, BadCreationRequestException, Exception {
		OdsOutputService service = new OdsOutputService();
		service.outputBottlesPriceList(insertDummyBeersAndProviders());
	}
	
	private List<BeerDTO> insertDummyBeersAndProviders() throws NoContentFoundException, BadCreationRequestException {
		ModelMapper modelMapper = new ModelMapper();
		
		Place eng = createAndSaveOrigin("Angleterre", "UK");
		Place sco = createAndSaveOrigin("Ecosse", "UK");
		Place vv = createAndSaveOrigin("Vevey", "CH");
		
		Producer robinsons = createAndSaveProducer("Robinson's Brewery", eng);
		Producer brewdog = createAndSaveProducer("Brewdog", sco);
		Producer fdb = createAndSaveProducer("FdB", vv);
		
		List<Beer> beers = new ArrayList<>();
		beers.add(createAndSaveBeer("Trooper Red 'N' Black", robinsons));
		beers.add(createAndSaveBeer("Dianemayte", fdb));
		beers.add(createAndSaveBeer("Black Hammer", brewdog));
		
		List<BeerDTO> beersDTO = new ArrayList<>();
		beers.forEach(beer -> beersDTO.add(modelMapper.map(beer, BeerDTO.class)));
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
    
    private Beer createAndSaveBeer(String name, Producer producer) throws BadCreationRequestException {
        Beer newBeer = new Beer();
        newBeer.setName(name);
        newBeer.setProducer(producer);
        newBeer.setAbv(0.05);
        newBeer.setIbu(30L);
        newBeer.setSrm(20L);
        return newBeer;
    }
}
