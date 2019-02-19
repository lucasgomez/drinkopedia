package ch.lgo.drinks.simple.resources;

import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.service.BeersServiceImpl;

@RestController
@CrossOrigin(origins={"*"})
@RequestMapping
public class PublicBeersResource {

    @Autowired
    private BeersServiceImpl beersService;
    
    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/beers")
    public BeersDTOList getBeers() {
        return beersService.getAll();
    }

    @GetMapping("/beers/{beer_id}")
    public ResponseEntity<?> getBeer(@PathVariable("beer_id") long beerId) {
        return beersService.loadById(beerId)
                .map(beer -> ResponseEntity.ok().body(beer))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            
    }

    @GetMapping("/beers/bars/{bar_id}")
    public ResponseEntity<?> loadBeersByBar(@PathVariable("bar_id") long barId) {
        return loadBeersByEntity(barId, beersService::loadBeersByBarId);
    }
    
    @GetMapping("/beers/colors/{beer_color_id}")
    public ResponseEntity<?> loadBeersByColor(@PathVariable("beer_color_id") long beerColorId) {
        return loadBeersByEntity(beerColorId, beersService::loadBeersByColorId);
    }
    
    @GetMapping("/beers/styles/{beer_style_id}")
    public ResponseEntity<?> loadBeersByStyle(@PathVariable("beer_style_id") long beerStyleId) {
        return loadBeersByEntity(beerStyleId, beersService::loadBeersByStyleId);
    }
    
    @GetMapping("/beers/producers/{beer_producer_id}")
    public ResponseEntity<?> loadBeersByProducer(@PathVariable("beer_producer_id") long beerProducerId)  {
        return loadBeersByEntity(beerProducerId, beersService::loadBeersByProducereId);
    }
    
    @GetMapping("/beers/origins/{beer_origin_id}")
    public ResponseEntity<?> loadBeersByOrigin(@PathVariable("beer_origin_id") long beerOriginId) {
        return loadBeersByEntity(beerOriginId, beersService::loadBeersByOriginId);
    }

    @GetMapping("/beers/search/{beer_name}")
    public BeersDTOList findBeersByName(@PathVariable("beer_name") String beerName) {
        return beersService.findByName(beerName);
    }
    
    private ResponseEntity<?> loadBeersByEntity(long entityId, Function<Long, Optional<BeersDTOList>> listLoader) {
        return listLoader.apply(entityId)
                .map(list -> ResponseEntity.ok().body(list))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
