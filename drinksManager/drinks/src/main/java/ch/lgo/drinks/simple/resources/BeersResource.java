package ch.lgo.drinks.simple.resources;

import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.BeerDataForEditDto;
import ch.lgo.drinks.simple.dto.BeerWithPricesDto;
import ch.lgo.drinks.simple.dto.BottleBeerDto;
import ch.lgo.drinks.simple.dto.TapBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Availability;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersService;

@RestController
@CrossOrigin(origins={"*"}, allowCredentials="true")
@RequestMapping("/private/beers")
public class BeersResource {

    @Autowired
    private BeersService beersService;
    
    @Context
    UriInfo uriInfo;

    @GetMapping("/{beer_id}")
    public ResponseEntity<?> getBeer(@PathVariable("beer_id") long beerId) {
        return beersService.loadByIdForEdit(beerId)
                .map(beer -> ResponseEntity.ok().body(beer))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/{beer_id}/tap")
    public ResponseEntity<?> getTapBeer(@PathVariable("beer_id") long beerId) {
        return ResponseEntity.ok()
                .body(beersService.loadTapByIdForEdit(beerId));
    }
    
    @GetMapping("/{beer_id}/bottle")
    public ResponseEntity<?> getBottleBeer(@PathVariable("beer_id") long beerId) {
        return ResponseEntity.ok()
                .body(beersService.loadBottleByIdForEdit(beerId));
    }
    
    @PostMapping
    public ResponseEntity<?> createBeer(BeerDTO newBeer) throws BadCreationRequestException {
//        Beer createdBeer = beersService.create(convertToEntity(newBeer));
//        URI location = uriInfo.getAbsolutePathBuilder().path("{id}")
//                .resolveTemplate("id", createdBeer.getId()).build();
//
//        return Response.created(location).build();
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("{beer_id}")
    public ResponseEntity<?> updateBeer(@PathVariable("beer_id") long beerId, @RequestBody BeerDataForEditDto beerToUpdate) {
        try {
            return ResponseEntity
                    .ok()
                    .body(beersService.update(beerId, beerToUpdate));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("{beer_id}/tap")
    public ResponseEntity<?> updateTapBeer(@PathVariable("beer_id") long beerId, @RequestBody TapBeerDto beerToUpdate) {
        try {
            return ResponseEntity
                    .ok()
                    .body(beersService.updateTap(beerId, beerToUpdate));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("{beer_id}/bottle")
    public ResponseEntity<?> updateBottleBeer(@PathVariable("beer_id") long beerId, @RequestBody BottleBeerDto beerToUpdate) {
        try {
            return ResponseEntity
                    .ok()
                    .body(beersService.updateBottle(beerId, beerToUpdate));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("{beer_id}")
    public ResponseEntity<?> deleteBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    
    @GetMapping("/bars/{bar_id}")
    public ResponseEntity<?> loadBeersByBar(@PathVariable("bar_id") long barId) {
        return loadBeersByEntity(barId, beersService::loadBeersWithPricesByBarId);
    }
    
    @GetMapping("/colors/{beer_color_id}")
    public ResponseEntity<?> loadBeersByColor(@PathVariable("beer_color_id") long beerColorId) {
        return loadBeersByEntity(beerColorId, beersService::loadBeersWithPricesByColorId);
    }
    
    @GetMapping("/styles/{beer_style_id}")
    public ResponseEntity<?> loadBeersByStyle(@PathVariable("beer_style_id") long beerStyleId) {
        return loadBeersByEntity(beerStyleId, beersService::loadBeersWithPricesByStyleId);
    }
    
    @GetMapping("/producers/{beer_producer_id}")
    public ResponseEntity<?> loadBeersByProducer(@PathVariable("beer_producer_id") long beerProducerId)  {
        return loadBeersByEntity(beerProducerId, beersService::loadBeersWithPricesByProducereId);
    }
    
    @GetMapping("/origins/{beer_origin_id}")
    public ResponseEntity<?> loadBeersByOrigin(@PathVariable("beer_origin_id") long beerOriginId) {
        return loadBeersByEntity(beerOriginId, beersService::loadBeersWithPricesByOriginId);
    }

    @PutMapping("{beer_id}/bottle/availability")
    private ResponseEntity<?> setBottleAvailability(@PathVariable("beer_id") long beerId, @RequestBody String availability) {
        try {
            return ResponseEntity
                    .ok()
                    .body(beersService.updateBottleAvailability(beerId, Availability.valueOf(availability)));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    @PutMapping("{beer_id}/tap/availability")
    public ResponseEntity<?> setTapAvailability(@PathVariable("beer_id") long beerId, @RequestBody String availability) {
        try {
            return ResponseEntity
                    .ok()
                    .body(beersService.updateTapAvailability(beerId, Availability.valueOf(availability)));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    private ResponseEntity<?> loadBeersByEntity(long entityId, Function<Long, Optional<BeersDTOList<BeerWithPricesDto>>> listLoader) {
        return listLoader.apply(entityId)
                .map(list -> ResponseEntity.ok().body(list))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
