package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Bar {

	private Long id;
	private String name;
	private Set<Drink> drinks = new HashSet<>();

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
	
	@OneToMany(mappedBy="bars")
	public Set<Drink> getDrinks() {
		return drinks;
	}
	public void setDrinks(Set<Drink> drinks) {
		this.drinks = drinks;
	}
}
