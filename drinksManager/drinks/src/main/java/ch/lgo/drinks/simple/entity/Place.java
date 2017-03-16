package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Place implements IHasId {

	private Long id;
	private String name;
	private String shortName;
	
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

	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortname) {
		this.shortName = shortname;
	}
}
