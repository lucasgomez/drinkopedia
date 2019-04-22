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
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.createTypeMap(BeerDataForEditDto.class, Beer.class)
            .addMappings(mapper -> mapper.skip(Beer::setTap))
            .addMappings(mapper -> mapper.skip(Beer::setStyle))
            .addMappings(mapper -> mapper.skip(Beer::setColor))
            .addMappings(mapper -> mapper.skip(Beer::setProducer))
            .addMappings(mapper -> mapper.skip(Beer::setBottle));
        
        modelMapper.createTypeMap(Beer.class, BeerDataForEditDto.class);
//            .addMappings(mapper -> mapper
//                    .when(ctx -> ((Beer) ctx.getSource()).getTap() == null)
//                    .map(beer -> new HashSet<Long>(), VeryDetailedBeerDto::setTapBarsIds))
//            .addMappings(mapper -> mapper
//                    .when(ctx -> ((Beer) ctx.getSource()).getBottle() == null)
//                    .map(beer -> new HashSet<Long>(), VeryDetailedBeerDto::setBottleBarsIds));

//        modelMapper.addMappings(skipPrices);
//        modelMapper.addMappings(skipForeignEntities);
//        modelMapper.addMappings(addStrength);
        return modelMapper;
    }
    
    private PropertyMap<BeerDataForEditDto, Beer> skipPrices = new PropertyMap<BeerDataForEditDto, Beer>() {
        @Override
        protected void configure() {
            skip().setBottle(null);
            skip().setTap(null);
        }
    };
    
    private PropertyMap<BeerDataForEditDto, Beer> skipForeignEntities = new PropertyMap<BeerDataForEditDto, Beer>() {
        @Override
        protected void configure() {
            skip().setProducer(null);
            skip().setColor(null);
            skip().setStyle(null);
        }
    };
    
    PropertyMap<BeerDataForEditDto, Beer> addStrength = new PropertyMap<BeerDataForEditDto, Beer>() {
        @Override
        protected void configure() {
            map(source).setBitterness(StrengthEnum.getStrengthByRank(source.getBitternessRank()));
            map(source).setHopping(StrengthEnum.getStrengthByRank(source.getHoppingRank()));
            map(source).setSweetness(StrengthEnum.getStrengthByRank(source.getSournessRank()));
            map(source).setSourness(StrengthEnum.getStrengthByRank(source.getSournessRank()));
        }
    };
}
