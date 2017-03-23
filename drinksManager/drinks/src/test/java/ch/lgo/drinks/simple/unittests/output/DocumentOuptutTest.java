package ch.lgo.drinks.simple.unittests.output;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import ch.lgo.drinks.simple.dto.DetailedPrintingDrinkDTO;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.entity.SellableBottledDrink;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.service.OdsOutputService;

public class DocumentOuptutTest {
	
	@Test
	public void exportPriceListOds() throws NoContentFoundException, BadCreationRequestException, Exception {
		OdsOutputService service = new OdsOutputService();
		service.outputBottlesPriceList(insertDummyBeersAndProviders());
	}
	
	private List<DetailedPrintingDrinkDTO> insertDummyBeersAndProviders() throws NoContentFoundException, BadCreationRequestException {
		ModelMapper modelMapper = new ModelMapper();
		
		Place eng = createAndSaveOrigin("Angleterre", "UK");
		Place sco = createAndSaveOrigin("Ecosse", "UK");
		Place vv = createAndSaveOrigin("Vevey", "CH");
		
		Producer robinsons = createAndSaveProducer("Robinson's Brewery", eng);
		Producer brewdog = createAndSaveProducer("Brewdog", sco);
		Producer fdb = createAndSaveProducer("FdB", vv);
		
		List<SellableBottledDrink> drinks = new ArrayList<>();
		drinks.add(createAndSaveSellingBeer("Trooper Red 'N' Black", robinsons, 5.5, 24, 6.0, 33));
		drinks.add(createAndSaveSellingBeer("Dianemayte", fdb, 8.0, 45, 5.5, 50));
		drinks.add(createAndSaveSellingBeer("Black Hammer", brewdog, 7.2, 42, 6.5, 33));
		
		List<DetailedPrintingDrinkDTO> beersDTO = new ArrayList<>();
		drinks.forEach(beer -> beersDTO.add(modelMapper.map(beer, DetailedPrintingDrinkDTO.class)));
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
    
    private SellableBottledDrink createAndSaveSellingBeer(String name, Producer producer, Double abv, Integer ibu, Double price, Integer volume) throws BadCreationRequestException {
        Beer newBeer = new Beer();
        newBeer.setName(name);
        newBeer.setProducer(producer);
        newBeer.setAbv(abv);
        newBeer.setIbu(30L);
        
        SellableBottledDrink sellableDrink = new SellableBottledDrink();
        sellableDrink.setBeer(newBeer);
        sellableDrink.setPrice(price);
        sellableDrink.setVolumeInCl(volume);
        return sellableDrink;
    }
}
