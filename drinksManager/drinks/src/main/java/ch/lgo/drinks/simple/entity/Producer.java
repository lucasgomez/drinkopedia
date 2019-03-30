package ch.lgo.drinks.simple.entity;

import static java.util.Comparator.comparing;

import java.util.Comparator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;

@Entity
public class Producer implements HasId, DescriptiveLabel, Comparable<Producer> {

	private Long id;
	private String name;
	private String comment;
    private Place origin;
	private Comparator<Producer> comparing = comparing(Producer::getName, Comparator.nullsLast(Comparator.naturalOrder()));

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
	
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
	
	public Producer(String name) {
		this.name = name;
	}
	
	public Producer() {
	}
	
	@Override
	public int compareTo(Producer o) {
		return comparing.compare(this, o);
	}
    
    public String toString() {
        return String.format("Id: %s, name: %s", getId(), getName());
    }
}
