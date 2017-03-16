package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

public interface ICrudRepository <E> {
	
    public E loadById(long id);
    public Collection<E> findAll();
    public List<E> findByName(String entityName);
    public void delete(long entityId);
    public E save(E entityToUpdate);
    public void deleteAll();
}
