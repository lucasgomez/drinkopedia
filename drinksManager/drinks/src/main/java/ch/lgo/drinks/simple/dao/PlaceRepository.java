package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.QPlace;

@Repository
@Transactional
public class PlaceRepository implements ICrudRepository<Place> {

    @PersistenceContext
    private EntityManager em;
    
	@Override
	public Place loadById(long id) {
        JPAQuery<Place> query = new JPAQuery<>(em);
        QPlace qPlace = QPlace.place;
        return query.from(qPlace).where(qPlace.id.eq(id)).fetchOne();
	}

	@Override
	public Collection<Place> findAll() {
		JPAQuery<Place> query = new JPAQuery<>(em);
		QPlace qPlace = QPlace.place;
		return query.from(qPlace).fetch();
	}

	@Override
	public List<Place> findByName(String placeName) {
		 JPAQuery<Place> query = new JPAQuery<>(em);
	        QPlace qPlace = QPlace.place;
	        return query.from(qPlace).where(qPlace.name.likeIgnoreCase("%"+placeName+"%")).fetch();

	}

	@Override
	public void delete(long placeId) {
		QPlace qPlace = QPlace.place;
        new JPADeleteClause(em, qPlace).where(qPlace.id.eq(placeId)).execute();
 	}

	@Override
	public Place save(Place placeToUpdate) {
	      return em.merge(placeToUpdate);
	}
	
	@Override
	public void deleteAll() {
        QPlace qPlace = QPlace.place;
        new JPADeleteClause(em, qPlace).execute();
	}

}
