package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;

@Entity
public class Bar implements HasId, DescriptiveLabel, Comparable<Bar> {
	private Long id;
	private String name;
	private Set<TapBeer> tapBeers = new HashSet<>();
	private Set<BottledBeer> bottledBeer = new HashSet<>();
    private String comment;

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
	
	public String getComment() {
	    return comment;
	}
	public void setComment(String comment) {
	    this.comment = comment;
	}
	
	@Override
	public int compareTo(Bar o) {
		return DescriptiveLabel.byName.compare(this, o);
	}
	
	@Transient
	public Bar addBottledBeer(BottledBeer bottledBeer) {
	    this.bottledBeer.add(bottledBeer);
	    return this;
	}
	
	@Transient
	public Bar addTapBeer(TapBeer tapBeer) {
	    this.tapBeers.add(tapBeer);
	    return this;
	}
    
    public String toString() {
        return String.format("Id: %s, name: %s", getId(), getName());
    }
}
