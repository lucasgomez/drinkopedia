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
    public List<DescriptiveLabelDto> getBars() {
        List<DescriptiveLabelDto> colors = beersService.findBarsList();
        return colors;
    }
    
    @GetMapping("/colors")
    public List<DescriptiveLabelDto> getColors() {
        List<DescriptiveLabelDto> colors = beersService.findColorsList();
        return colors;
    }
    
    @GetMapping("/styles")
    public List<DescriptiveLabelDto> getStyles() {
        List<DescriptiveLabelDto> colors = beersService.findStylesList();
        return colors;
    }

    @GetMapping("/producers")
    public List<DescriptiveLabelDto> getProducers() {
        List<DescriptiveLabelDto> producers = beersService.findProducersList();
        return producers;
    }
    
    @GetMapping("/origins")
    public List<DescriptiveLabelDto> getOrigins() {
        List<DescriptiveLabelDto> places = beersService.findPlacesList();
        return places;
    }

}
