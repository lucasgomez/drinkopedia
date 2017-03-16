package ch.lgo.drinks.simple.service;

import ch.lgo.drinks.simple.dao.ICrudRepository;
import ch.lgo.drinks.simple.entity.IHasId;
import ch.lgo.drinks.simple.exceptions.BadCreationRequestException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

public abstract class AbstractCrudService<E extends IHasId> {

	
	protected abstract ICrudRepository<E> getCrudRepository();
	
	public E create(E newE) throws BadCreationRequestException {
		return getCrudRepository().save(newE);
	}

	public E update(E updatedE) throws ResourceNotFoundException, BadCreationRequestException {
		E producerToUpdate = getCrudRepository().loadById(updatedE.getId());
		if (producerToUpdate != null) {
			return getCrudRepository().save(updatedE);
		} else {
			throw new ResourceNotFoundException("Entity of id " + updatedE.getId() + " does not exists");
		}
	}

	public E loadById(long producerId) throws ResourceNotFoundException {
		E producer = getCrudRepository().loadById(producerId);
		if (producer != null) {
			return producer;
		} else {
			throw new ResourceNotFoundException("E of id " + producerId + " does not exists");
		}
	}

	public void delete(long producerId) throws ResourceNotFoundException {
		getCrudRepository().delete(producerId);
	}
}
