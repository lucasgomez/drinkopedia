package ch.lgo.drinks.simple.dto;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
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
            .addMappings(mapper -> mapper.skip(Beer::setStyle))
            .addMappings(mapper -> mapper.skip(Beer::setColor))
            .addMappings(mapper -> mapper.skip(Beer::setProducer))
            .addMappings(mapper -> mapper.using(strengthValueToEnum).map(BeerDataForEditDto::getBitternessRank, Beer::setBitterness))
            .addMappings(mapper -> mapper.using(strengthValueToEnum).map(BeerDataForEditDto::getHoppingRank, Beer::setHopping))
            .addMappings(mapper -> mapper.using(strengthValueToEnum).map(BeerDataForEditDto::getSweetnessRank, Beer::setSweetness))
            .addMappings(mapper -> mapper.using(strengthValueToEnum).map(BeerDataForEditDto::getSournessRank, Beer::setSourness));
        
        modelMapper.createTypeMap(Beer.class, BeerDataForEditDto.class);
        
//        modelMapper.createTypeMap(TapBeer.class, TapBeerDto.class)
//            .addMappings(mapper -> mapper.when(ctx -> 
//                                            Optional.ofNullable(((TapBeer) ctx.getSource()).getBars())
//                                                .map(Set::isEmpty)
//                                                .orElse(false))
//                                         .map(TapBeer::getBars,
//                                              (tap, mu) -> tap.setBarsId(Collections.emptySet())));
            
        return modelMapper;
    }
    
    
    private Converter<String, StrengthEnum> strengthValueToEnum = new Converter<String, StrengthEnum>() {

        @Override
        public StrengthEnum convert(MappingContext<String, StrengthEnum> context) {
            return StrengthEnum.getStrengthByRank(context.getSource());
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
