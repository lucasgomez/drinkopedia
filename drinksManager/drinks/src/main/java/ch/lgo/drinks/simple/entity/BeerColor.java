package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dao.NamedEntity;

@Entity
public class BeerColor implements HasId, NamedEntity {
	
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    
	public Long getId() {
        return id;
    }
    public BeerColor setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    public BeerColor setName(String name) {
        this.name = name;
        return this;
    }
    
    public BeerColor(String name) {
    	this.name = name;
    }
    
    public BeerColor() {
    }
}
