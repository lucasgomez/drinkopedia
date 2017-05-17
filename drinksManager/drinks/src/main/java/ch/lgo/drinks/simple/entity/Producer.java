package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import ch.lgo.drinks.simple.dao.NamedEntity;

@Entity
public class Producer implements HasId, NamedEntity {

	private Long id;
	private String name;	
	private Place origin;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public Producer setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Producer setName(String name) {
		this.name = name;
		return this;
	}
	
	@ManyToOne
	public Place getOrigin() {
		return origin;
	}
	public Producer setOrigin(Place origin) {
		this.origin = origin;
		return this;
	}
	
	public Producer(String name) {
		this.name = name;
	}
	
	public Producer() {
	}
}
