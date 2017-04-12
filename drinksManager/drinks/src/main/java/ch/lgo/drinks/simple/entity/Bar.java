package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Bar {

	private Long id;
	private String name;
	private Set<Beer> beers = new HashSet<>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany
	public Set<Beer> getBeers() {
		return beers;
	}
	public void setBeers(Set<Beer> beers) {
		this.beers = beers;
	}
}
