package ch.lgo.drinks.simple.dao;

import java.util.Comparator;

public interface DescriptiveLabel {
	String getName();
	String getComment();
	
	public Comparator<DescriptiveLabel> byName = Comparator.comparing(DescriptiveLabel::getName, Comparator.nullsLast(Comparator.naturalOrder()));
}
