package ch.lgo.drinks.simple.entity;

import java.util.Set;

public interface HasBar<E> {
    public Set<Bar> getBars();
    public E setBars(Set<Bar> bars);
    public Set<Long> getBarsIds();
}
