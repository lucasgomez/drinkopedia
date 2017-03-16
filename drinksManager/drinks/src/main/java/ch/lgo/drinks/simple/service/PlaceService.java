package ch.lgo.drinks.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.dao.PlaceRepository;
import ch.lgo.drinks.simple.entity.Place;

@Service
public class PlaceService extends AbstractCrudService<Place> {

	@Autowired
	PlaceRepository placeRepository;

	@Override
	protected ICrudRepository<Place> getCrudRepository() {
		return placeRepository;
	}

}
