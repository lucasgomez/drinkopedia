package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dao.NamedEntity;

@Entity
public class Place implements HasId, NamedEntity {

	private Long id;
	private String name;
	private String shortName;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public Place setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Place setName(String name) {
		this.name = name;
		return this;
	}

	public String getShortName() {
		return shortName;
	}
	public Place setShortName(String shortname) {
		this.shortName = shortname;
		return this;
	}
}
