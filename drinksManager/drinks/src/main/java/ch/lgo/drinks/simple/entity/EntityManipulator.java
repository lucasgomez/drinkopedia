package ch.lgo.drinks.simple.entity;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import ch.lgo.drinks.simple.dto.DetailedBeerDto;

public class EntityManipulator<E extends HasId> {
    
    private Function<DetailedBeerDto, Optional<Long>> dtoToForeignEntityIdMapper;
    private Function<Beer, E> foreignEntityGetter;
    private Function<Beer, Optional<Long>> foreignEntityIdGetter;
    private BiFunction<Beer, Optional<Long>, Beer> foreignEntitySetter;
    private Optional<Long> newId;
    
    public Function<DetailedBeerDto, Optional<Long>> getDtoToForeignEntityIdMapper() {
        return dtoToForeignEntityIdMapper;
    }
    public Function<Beer, E> getForeignEntityGetter() {
        return foreignEntityGetter;
    }
    public Function<Beer, Optional<Long>> getForeignEntityIdGetter() {
        return foreignEntityIdGetter;
    }
    public BiFunction<Beer, Optional<Long>, Beer> getForeignEntitySetter() {
        return foreignEntitySetter;
    }
    public Optional<Long> getNewId() {
        return newId;
    }
    public EntityManipulator<E> setNewId(Optional<Long> newId) {
        this.newId = newId;
        return this;
    }
    
    public EntityManipulator(
            Function<DetailedBeerDto, Optional<Long>> dtoToForeignEntityIdMapper, 
            Function<Beer, E> entityToForeignEntityMapper, 
            BiFunction<Beer, Optional<Long>, Beer> setter) {
        this.dtoToForeignEntityIdMapper = dtoToForeignEntityIdMapper;
        this.foreignEntityGetter = entityToForeignEntityMapper;
        this.foreignEntitySetter = setter;
        this.foreignEntityIdGetter = (beer) -> Optional.ofNullable(Optional.ofNullable(foreignEntityGetter.apply(beer)).map(HasId::getId).orElse(null));
    }
}
