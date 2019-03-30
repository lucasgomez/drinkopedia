package ch.lgo.drinks.simple.entity;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityManipulator<E extends HasId> {
    private Function<Beer, E> getter;
    private BiFunction<Beer, Long, Beer> setter;
    private Long newId;
    
    public Function<Beer, E> getGetter() {
        return getter;
    }
    public BiFunction<Beer, Long, Beer> getSetter() {
        return setter;
    }
    public Long getNewId() {
        return newId;
    }
    
    public EntityManipulator(Function<Beer, E> getter, BiFunction<Beer, Long, Beer> setter, Long newId) {
        this.getter = getter;
        this.setter = setter;
        this.newId = newId;
    }
}
