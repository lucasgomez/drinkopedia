package ch.lgo.drinks.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.entity.Producer;

@Service
public class ProducerService extends AbstractCrudService<Producer> {

	@Autowired ProducerRepository producerRepository;
	@Override
	protected ICrudRepository<Producer> getCrudRepository() {
		return producerRepository;
	}

}
