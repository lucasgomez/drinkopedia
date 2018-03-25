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
import ch.lgo.drinks.simple.entity.QBeerColor;
import ch.lgo.drinks.simple.entity.QBeerStyle;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QPlace;
import ch.lgo.drinks.simple.entity.QProducer;
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

	public List<Beer> findAllWithServices() {
		JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
        return query
        		.from(qBeer)
        		.leftJoin(qBeer.tap, tapBeer)
        		.leftJoin(qBeer.bottle, bottledBeer)
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
    	Beer beer = loadById(bottledBeer.getBeer().getId());
    	bottledBeer.setBeer(beer);
    	em.persist(bottledBeer);
		return loadById(bottledBeer.getBeer().getId());
	}

	public Beer addTapBeer(TapBeer tapBeer) {
    	Beer beer = loadById(tapBeer.getBeer().getId());
    	tapBeer.setBeer(beer);
		em.persist(tapBeer);
		//TODO Should return an entity where tap is fetched!
		return loadById(tapBeer.getBeer().getId());
	}

	public void clearService() {
		QTapBeer tapBeer = QTapBeer.tapBeer;
		new JPADeleteClause(em, tapBeer).execute();
		QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
		new JPADeleteClause(em, bottledBeer).execute();
	}

	public TapBeer save(TapBeer tap) {
		return em.merge(tap);
	}

	public BottledBeer save(BottledBeer tap) {
		return em.merge(tap);
	}

    public List<Beer> findByStyle(long styleId) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeerStyle qBeerStyle = QBeerStyle.beerStyle;
        QBeer qBeer = QBeer.beer;
        return query
                .from(qBeer)
                .innerJoin(qBeer.style, qBeerStyle).fetchJoin()
                .where(qBeerStyle.id.eq(styleId))
                .fetch();
    }

    public List<Beer> findByColor(long colorId) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeerColor qBeerColor = QBeerColor.beerColor;
        QBeer qBeer = QBeer.beer;
        return query
                .from(qBeer)
                .innerJoin(qBeer.color, qBeerColor).fetchJoin()
                .where(qBeerColor.id.eq(colorId))
                .fetch();

    }

    public List<Beer> findByProducer(long producerId) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        QBeer qBeer = QBeer.beer;
        return query
                .from(qBeer)
                .innerJoin(qBeer.producer, qProducer).fetchJoin()
                .where(qProducer.id.eq(producerId))
                .fetch();

    }

    public List<Beer> findByOrigin(long originId) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        QPlace qPlace = QPlace.place;
        QBeer qBeer = QBeer.beer;
        return query
                .from(qBeer)
                .innerJoin(qBeer.producer, qProducer).fetchJoin()
                .innerJoin(qProducer.origin, qPlace).fetchJoin()
                .where(qPlace.id.eq(originId))
                .fetch();

    }

}
