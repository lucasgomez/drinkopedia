package ch.lgo.drinks.simple.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.lgo.drinks.simple.dto.DescriptiveLabelDto;
import ch.lgo.drinks.simple.service.BeersService;

@RestController
@RequestMapping("/public/lists")
public class ListsResource {

    @Autowired
    private BeersService beersService;
    
    @GetMapping("/bars")
    public List<DescriptiveLabelDto> getBarsWithService() {
        List<DescriptiveLabelDto> colors = beersService.findBarsList(true);
        return colors;
    }
    
    @GetMapping("/colors")
    public List<DescriptiveLabelDto> getColorsWithService() {
        List<DescriptiveLabelDto> colors = beersService.findColorsList(true);
        return colors;
    }
    
    @GetMapping("/styles")
    public List<DescriptiveLabelDto> getStylesWithService() {
        List<DescriptiveLabelDto> colors = beersService.findStylesList(true);
        return colors;
    }

    @GetMapping("/producers")
    public List<DescriptiveLabelDto> getProducersWithService() {
        List<DescriptiveLabelDto> producers = beersService.findProducersList(true);
        return producers;
    }
    
    @GetMapping("/origins")
    public List<DescriptiveLabelDto> getOriginsWithService() {
        List<DescriptiveLabelDto> places = beersService.findPlacesList(true);
        return places;
    }
    
    @GetMapping("/bars/all")
    public List<DescriptiveLabelDto> getAllBars() {
        List<DescriptiveLabelDto> colors = beersService.findBarsList(false);
        return colors;
    }
    
    @GetMapping("/colors/all")
    public List<DescriptiveLabelDto> getAllColors() {
        List<DescriptiveLabelDto> colors = beersService.findColorsList(false);
        return colors;
    }
    
    @GetMapping("/styles/all")
    public List<DescriptiveLabelDto> getAllStyles() {
        List<DescriptiveLabelDto> colors = beersService.findStylesList(false);
        return colors;
    }
    
    @GetMapping("/producers/all")
    public List<DescriptiveLabelDto> getAllProducers() {
        List<DescriptiveLabelDto> producers = beersService.findProducersList(false);
        return producers;
    }
    
    @GetMapping("/origins/all")
    public List<DescriptiveLabelDto> getAllOrigins() {
        List<DescriptiveLabelDto> places = beersService.findPlacesList(false);
        return places;
    }

}
