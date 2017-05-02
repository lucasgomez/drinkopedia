package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;
import ch.lgo.drinks.simple.entity.TapBeer;

@Repository
@Transactional
public class BeersRepository {

    @PersistenceContext
    private EntityManager em;
    
    public Beer loadById(long id) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query
        		.from(qBeer)
        		.where(qBeer.id.eq(id))
        		.fetchOne();
    }

    public Collection<Beer> findAll() {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query
        		.from(qBeer)
        		.fetch();
    }

    public List<Beer> findByName(String beerName) {
        //TODO Something like the google search of NJ instead of exact match ignore case
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query
        		.from(qBeer)
        		.where(qBeer.name.likeIgnoreCase("%"+beerName+"%"))
        		.fetch();
    }
    
    public Beer loadByExternalCode(String externalCode) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query
        		.from(qBeer)
        		.where(qBeer.externalId.equalsIgnoreCase(externalCode))
        		.fetchOne();
    }
    
    public List<Beer> loadTapBeers() {
    	JPAQuery<Beer> query = new JPAQuery<>(em);
    	QBeer qBeer = QBeer.beer;
    	QTapBeer qTapBeer = QTapBeer.tapBeer;
    	return query
    			.from(qBeer)
    			.innerJoin(qBeer.tap, qTapBeer)
    			.fetch();
    }
    
    public List<Beer> loadBottledBeers() {
    	JPAQuery<Beer> query = new JPAQuery<>(em);
    	QBeer qBeer = QBeer.beer;
    	QBottledBeer qBottledBeer = QBottledBeer.bottledBeer;
    	return query
    			.from(qBeer)
    			.innerJoin(qBottledBeer)
    			.fetch();
    }

    public void delete(long beerId) {
        QBeer qBeer = QBeer.beer;
        new JPADeleteClause(em, qBeer)
        	.where(qBeer.id.eq(beerId))
        	.execute();
    }

    public boolean exists(long beerId) {
        //TODO Useless and resources consuming -> delete or turn into exists()
        return loadById(beerId) != null;
    }

    public Beer save(Beer beerToUpdate) {
        return em.merge(beerToUpdate);
    }

    public void deleteAll() {
        QBeer qBeer = QBeer.beer;
        new JPADeleteClause(em, qBeer).execute();
    }

	public Beer addBottledBeer(BottledBeer bottledBeer) {
    	Beer beer = bottledBeer.getBeer();
    	em.refresh(beer);
    	bottledBeer.setBeer(beer);
    	
		BottledBeer mergedBottle = em.merge(bottledBeer);
		beer.setBottle(bottledBeer);
		return em.merge(beer);
	}

	public Beer addTapBeer(TapBeer tapBeer) {
    	Beer beer = loadById(tapBeer.getBeer().getId());
    	tapBeer.setBeer(beer);
		em.persist(tapBeer);
		TapBeer find = em.find(TapBeer.class, tapBeer.getId());
		return loadById(tapBeer.getBeer().getId());
	}

}
