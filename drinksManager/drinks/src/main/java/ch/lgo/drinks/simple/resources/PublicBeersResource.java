package ch.lgo.drinks.simple.resources;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.service.BeersService;

@RestController
@RequestMapping("/public/beers")
public class PublicBeersResource {

    @Autowired
    private BeersService beersService;
    
    @PutMapping("/test")
    public ResponseEntity<?> testPost(@RequestBody String testString) {
        System.out.println("Put passé");
        return ResponseEntity.ok().body("Mu!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testPostByGet(@RequestParam String searchedText) {
        return testPost(searchedText);
    }
    
    @GetMapping("/")
    public BeersDTOList<DetailedBeerDto> getBeers() {
        return beersService.getAll();
    }

    @GetMapping("/{beer_id}")
    public ResponseEntity<?> getBeer(@PathVariable("beer_id") long beerId) {
        return beersService.loadById(beerId)
                .map(beer -> ResponseEntity.ok().body(beer))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/bars/{bar_id}")
    public ResponseEntity<?> loadBeersByBar(@PathVariable("bar_id") long barId) {
        return loadBeersByEntity(barId, beersService::loadBeersByBarId);
    }
    
    @GetMapping("/colors/{beer_color_id}")
    public ResponseEntity<?> loadBeersByColor(@PathVariable("beer_color_id") long beerColorId) {
        return loadBeersByEntity(beerColorId, beersService::loadBeersByColorId);
    }
    
    @GetMapping("/styles/{beer_style_id}")
    public ResponseEntity<?> loadBeersByStyle(@PathVariable("beer_style_id") long beerStyleId) {
        return loadBeersByEntity(beerStyleId, beersService::loadBeersByStyleId);
    }
    
    @GetMapping("/producers/{beer_producer_id}")
    public ResponseEntity<?> loadBeersByProducer(@PathVariable("beer_producer_id") long beerProducerId)  {
        return loadBeersByEntity(beerProducerId, beersService::loadBeersByProducereId);
    }
    
    @GetMapping("/origins/{beer_origin_id}")
    public ResponseEntity<?> loadBeersByOrigin(@PathVariable("beer_origin_id") long beerOriginId) {
        return loadBeersByEntity(beerOriginId, beersService::loadBeersByOriginId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findBeersByName(@RequestParam String searchedText) {
        return ResponseEntity.ok().body(beersService.findByName(searchedText));
    }
    
    private ResponseEntity<?> loadBeersByEntity(long entityId, Function<Long, Optional<BeersDTOList<DetailedBeerDto>>> listLoader) {
        return listLoader.apply(entityId)
                .map(list -> ResponseEntity.ok().body(list))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
