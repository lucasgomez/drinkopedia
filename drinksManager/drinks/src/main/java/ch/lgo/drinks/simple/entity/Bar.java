package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import ch.lgo.drinks.simple.dao.NamedEntity;

@Entity
public class Bar implements HasId, NamedEntity, Comparable<Bar> {
	//TODO Correct naming of bottledBeer and remove unused tables and columns from DB 
	private Long id;
	private String name;
	private Set<TapBeer> tapBeers = new HashSet<>();
	private Set<BottledBeer> bottledBeer = new HashSet<>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public Bar setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToMany(fetch=FetchType.LAZY)
	public Set<TapBeer> getTapBeers() {
		return tapBeers;
	}
	public Bar setTapBeers(Set<TapBeer> tapBeers) {
		this.tapBeers = tapBeers;
		return this;
	}
	
	@ManyToMany(fetch=FetchType.LAZY)
	public Set<BottledBeer> getBottledBeer() {
		return bottledBeer;
	}
	public Bar setBottledBeer(Set<BottledBeer> bottledBeer) {
		this.bottledBeer = bottledBeer;
		return this;
	}
	
	@Override
	public int compareTo(Bar o) {
		return NamedEntity.byName.compare(this, o);
	}
}
