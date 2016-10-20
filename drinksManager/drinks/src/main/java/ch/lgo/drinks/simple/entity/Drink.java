package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Drink {

	private Long id;
	private String name;
	private String producerName;
//	private ProducerEntity producer;
	
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

	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	
//	public ProducerEntity getProducer() {
//		return producer;
//	}
//	public void setProducer(ProducerEntity producer) {
//		this.producer = producer;
//	}
	
	public Drink() {
	}
		
	public Drink(String name) {
		this.name = name;
	}
}
