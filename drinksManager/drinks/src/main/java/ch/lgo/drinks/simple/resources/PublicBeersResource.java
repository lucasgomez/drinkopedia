package ch.lgo.drinks.simple.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dao.BeerColorService;
import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BarService;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import ch.lgo.drinks.simple.service.PlaceService;
import ch.lgo.drinks.simple.service.ProducerService;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins={"*"})
@RequestMapping
public class PublicBeersResource {

    @Autowired
    private BeersServiceImpl beersService;
    @Autowired
    private BarService barService;
    @Autowired
    private BeerColorService colorService;
    @Autowired
    private BeerStyleService styleService;
    @Autowired
    private ProducerService producerService;
    @Autowired
    private PlaceService placeService;
    
    
    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    //TODO Replace Response by DTO
    @GetMapping("/beers")
    @ApiOperation(value = "Find all beers")
    public BeersDTOList getBeers() throws NoContentFoundException {
        List<Beer> beers = beersService.getAll();
        return convertToBeersListDTO(beers);
    }

    @GetMapping("/beers/{beer_id}")
    public DetailedBeerDto getBeer(@PathVariable("beer_id") long beerId) throws ResourceNotFoundException {
        Beer beer = beersService.loadById(beerId);
        return convertToDetailedDto(beer);
    }

    @GetMapping("/beers/bars/{bar_id}")
    public BeersDTOList findBeersByBar(@PathVariable("bar_id") long barId) throws NoContentFoundException, ResourceNotFoundException {
        Bar bar = barService.loadById(barId);
        BeersDTOList beersFound = convertToBeersListDTO(beersService.findByBarId(barId), bar.getName(), bar.getComment());
        return beersFound;
    }
    
    @GetMapping("/beers/colors/{beer_color_id}")
    public BeersDTOList findBeersByColor(@PathVariable("beer_color_id") long beerColorId) throws NoContentFoundException, ResourceNotFoundException {
        BeerColor color = colorService.loadById(beerColorId);
        BeersDTOList beersFound = convertToBeersListDTO(beersService.findByColorId(beerColorId), color.getName(), color.getComment());
        return beersFound;
    }
    
    @GetMapping("/beers/styles/{beer_style_id}")
    public BeersDTOList findBeersByStyle(@PathVariable("beer_style_id") long beerStyleId) throws NoContentFoundException, ResourceNotFoundException {
        BeerStyle style = styleService.loadById(beerStyleId);
        BeersDTOList beersFound = convertToBeersListDTO(beersService.findByStyleId(beerStyleId), style.getName(), style.getComment());
        return beersFound;
    }
    
    @GetMapping("/beers/producers/{beer_producer_id}")
    public BeersDTOList findBeersByProducer(@PathVariable("beer_producer_id") long beerProducerId)
            throws NoContentFoundException, ResourceNotFoundException {
        Producer producer = producerService.loadById(beerProducerId);
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByProducereId(beerProducerId), producer.getName(), producer.getComment());
        return beersFound;
    }
    
    @GetMapping("/beers/origins/{beer_origin_id}")
    public BeersDTOList findBeersByOrigin(@PathVariable("beer_origin_id") long beerOriginId) throws NoContentFoundException, ResourceNotFoundException {
        Place place = placeService.loadById(beerOriginId);
        BeersDTOList beersFound = convertToBeersListDTO(beersService.findByOriginId(beerOriginId), place.getName(), place.getComment());
        return beersFound;
    }

    @GetMapping("/beers/search/{beer_name}")
    public BeersDTOList findBeersByName(@PathVariable("beer_name") String beerName)
            throws NoContentFoundException {
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByName(beerName));
        return beersFound;
    }
    
    @GetMapping("/bars/list")
    public List<DescriptiveLabelDto> getBars() throws NoContentFoundException {
        List<DescriptiveLabelDto> colors = beersService.findBarsList();
        return colors;
    }
    
    @GetMapping("/colors/list")
    public List<DescriptiveLabelDto> getColors()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> colors = beersService.findColorsList();
        return colors;
    }
    
    @GetMapping("/styles/list")
    public List<DescriptiveLabelDto> getStyles()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> colors = beersService.findStylesList();
        return colors;
    }

    @GetMapping("/producers/list")
    public List<DescriptiveLabelDto> getProducers()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> producers = beersService.findProducersList();
        return producers;
    }
    
    @GetMapping("/origins/list")
    public List<DescriptiveLabelDto> getOrigins()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> places = beersService.findPlacesList();
        return places;
    }

    private BeersDTOList convertToBeersListDTO(List<Beer> beers) {
        //TODO replace placeholder
        return convertToBeersListDTO(beers, "Bier über alles", "Ce commentaire ne devrait pas apparaitre, mais au pire, peu m'en chaut.");
    }
    
    private BeersDTOList convertToBeersListDTO(List<Beer> beers, String name, String description) {
        BeersDTOList beersDTOList = new BeersDTOList();
        List<BeerDTO> beersList = beers.stream().map(beer -> convertToDto(beer))
                .collect(Collectors.toList());
        beersDTOList.setBeers(beersList);
        beersDTOList.setName(name);
        beersDTOList.setDescription(description);
        return beersDTOList;
    }

    private DetailedBeerDto convertToDetailedDto(Beer beer) {
        DetailedBeerDto beerDTO = modelMapper.map(beer, DetailedBeerDto.class);
        return beerDTO;
    }
    
    private BeerDTO convertToDto(Beer beer) {
        BeerDTO beerDTO = modelMapper.map(beer, BeerDTO.class);
        return beerDTO;
    }
}
