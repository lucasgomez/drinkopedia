package ch.lgo.drinks.simple.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;
import ch.lgo.drinks.simple.service.BeersServiceImpl;
import io.swagger.annotations.ApiOperation;

@RestController

@CrossOrigin(origins={"http://localhost:3000"})
@RequestMapping
public class BeersResource {

    @Autowired
    private BeersServiceImpl beersService;
    
    @Context
    UriInfo uriInfo;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Find all beers")
    public Response getBeers() throws NoContentFoundException {
        List<Beer> beers = beersService.getAll();
        return Response.ok().entity(convertToBeersListDTO(beers)).build();
    }

    @GetMapping("{beer_id}")
    public Response getBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        Beer beer = beersService.loadById(beerId);
        return Response.ok().entity(convertToDto(beer)).build();
    }

    @GetMapping("types/{beer_style_id}")
    public Response findBeersByStyle(
            @PathParam("beer_style_id") long beerStyleId)
            throws NoContentFoundException {
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByStyleId(beerStyleId));
        return Response.ok().entity(beersFound).build();
    }

    @GetMapping("search/{beer_name}")
    public Response findBeersByName(@PathParam("beer_name") String beerName)
            throws NoContentFoundException {
        BeersDTOList beersFound = convertToBeersListDTO(
                beersService.findByName(beerName));

        return Response.ok().entity(beersFound).build();
    }
    
    @GetMapping("beers/colors/list")
    public Response getColors()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> colors = beersService.findColorList();
        
        return Response.ok().entity(colors).build();
    }
    
    @GetMapping("styles/list")
    public Response getStyles()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> colors = beersService.findStyleList();
        
        return Response.ok().entity(colors).build();
    }

    
    @GetMapping("producers/list")
    public Response getProducers()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> producers = beersService.findProducerList();
        
        return Response.ok().entity(producers).build();
    }
    
    @GetMapping("places/list")
    public Response getPlaces()
            throws NoContentFoundException {
        List<DescriptiveLabelDto> places = beersService.findPlaceList();
        
        return Response.ok().entity(places).build();
    }
    
    @PostMapping
    public Response createBeer(BeerDTO newBeer) throws BadCreationRequestException {
        Beer createdBeer = beersService.create(convertToEntity(newBeer));
        URI location = uriInfo.getAbsolutePathBuilder().path("{id}")
                .resolveTemplate("id", createdBeer.getId()).build();

        return Response.created(location).build();
    }

    @PutMapping("{beer_id}")
    public Response updateBeer(@PathParam("beer_id") long beerId,
            BeerDTO beerToUpdate) throws ResourceNotFoundException, BadCreationRequestException {
        return Response.ok().entity(
                    beersService.update(beerId, convertToEntity(beerToUpdate)))
                    .build();
    }

    @DeleteMapping("{beer_id}")
    public Response deleteBeer(@PathParam("beer_id") long beerId)
            throws ResourceNotFoundException {
        beersService.delete(beerId);
        return Response.ok().build();
    }

    private BeersDTOList convertToBeersListDTO(List<Beer> beers) {
        BeersDTOList beersDTOList = new BeersDTOList();
        List<BeerDTO> beersList = beers.stream().map(beer -> convertToUselessDto(beer))
                .collect(Collectors.toList());
        beersDTOList.setBeers(beersList);
        return beersDTOList;
    }

    private Beer convertToEntity(BeerDTO postedBeer) {
        Beer beer = modelMapper.map(postedBeer, Beer.class);
        return beer;
    }

    private DetailedBeerDto convertToDto(Beer beer) {
        DetailedBeerDto beerDTO = modelMapper.map(beer, DetailedBeerDto.class);
        return beerDTO;
    }
    
    private BeerDTO convertToUselessDto(Beer beer) {
        BeerDTO beerDTO = modelMapper.map(beer, BeerDTO.class);
        return beerDTO;
    }
}
