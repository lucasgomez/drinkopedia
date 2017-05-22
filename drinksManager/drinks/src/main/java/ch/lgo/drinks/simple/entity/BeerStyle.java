package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dao.NamedEntity;

@Entity
public class BeerStyle implements HasId, NamedEntity {
    
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    
	public Long getId() {
        return id;
    }
    public BeerStyle setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    public BeerStyle setName(String name) {
        this.name = name;
        return this;
    }

	public BeerStyle() {
	}

	public BeerStyle(String styleName) {
		this.name = styleName;
	}
}
