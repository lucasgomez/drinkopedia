package ch.lgo.drinks.simple.dto;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.StrengthEnum;

@Configuration
public class MapperConfig {
    
    @Bean(name = "beerFieldsMapper")
    public ModelMapper getBeerFieldsMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(skipPrices);
        modelMapper.addMappings(skipForeignEntities);
        modelMapper.addMappings(addStrength);
        return modelMapper;
    }
    
    private PropertyMap<DetailedBeerDto, Beer> skipPrices = new PropertyMap<DetailedBeerDto, Beer>() {
        @Override
        protected void configure() {
            skip().setBottle(null);
            skip().setTap(null);
        }
    };
    
    private PropertyMap<DetailedBeerDto, Beer> skipForeignEntities = new PropertyMap<DetailedBeerDto, Beer>() {
        @Override
        protected void configure() {
            skip().setProducer(null);
            skip().setColor(null);
            skip().setStyle(null);
        }
    };
    
    PropertyMap<DetailedBeerDto, Beer> addStrength = new PropertyMap<DetailedBeerDto, Beer>() {
        @Override
        protected void configure() {
            map(source).setBitterness(StrengthEnum.getStrengthByRank(source.getBitternessRank()));
            map(source).setHopping(StrengthEnum.getStrengthByRank(source.getHoppingRank()));
            map(source).setSweetness(StrengthEnum.getStrengthByRank(source.getSournessRank()));
            map(source).setSourness(StrengthEnum.getStrengthByRank(source.getSournessRank()));
        }
    };
}
