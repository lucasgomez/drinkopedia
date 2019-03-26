package ch.lgo.drinks.simple.service;

import java.util.Optional;

import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

public abstract class AbstractCrudService<E extends HasId> {

	
	protected abstract ICrudRepository<E> getCrudRepository();
	
	public E create(E newE) throws BadCreationRequestException {
		return getCrudRepository().save(newE);
	}

	public E update(E updatedE) throws ResourceNotFoundException, BadCreationRequestException {
		return getCrudRepository().save(getCrudRepository().loadById(updatedE.getId())
		        .orElseThrow(ResourceNotFoundException::new));
	}

	public Optional<E> loadById(long producerId) {
		return getCrudRepository().loadById(producerId);
	}

	public void delete(long producerId) throws ResourceNotFoundException {
		getCrudRepository().delete(producerId);
	}
}
