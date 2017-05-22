package ch.lgo.drinks.simple.dao;

import java.util.Comparator;

import ch.lgo.drinks.simple.entity.Bar;

public interface NamedEntity {
	String getName();
	
	public Comparator<Bar> byName = Comparator.comparing(NamedEntity::getName, Comparator.nullsLast(Comparator.naturalOrder()));
}
