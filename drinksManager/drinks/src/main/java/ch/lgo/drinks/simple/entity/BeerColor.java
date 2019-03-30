package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;

@Entity
public class BeerColor implements HasId, DescriptiveLabel {
	
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String comment;
    
	public Long getId() {
        return id;
    }
    public BeerColor setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }
    public BeerColor setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public BeerColor(String name) {
        this.name = name;
    }
    
    public BeerColor() {
    }
    
    public String toString() {
        return String.format("Id: %s, name: %s", getId(), getName());
    }
}
