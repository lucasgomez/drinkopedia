package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBeerColor;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;

@Repository
@Transactional
public class BeerColorRepository implements ICrudRepository<BeerColor> {

    @PersistenceContext
    private EntityManager em;
    
    public BeerColor loadById(long id) {
    	JPAQuery<BeerColor> query = new JPAQuery<>(em);
    	QBeerColor qColor = QBeerColor.beerColor;
    	return query.from(qColor).where(qColor.id.eq(id)).fetchOne();
    }

    public Collection<BeerColor> findAll() {
    	JPAQuery<BeerColor> query = new JPAQuery<>(em);
    	QBeerColor qColor = QBeerColor.beerColor;
    	return query.from(qColor).fetch();
    }
    
    public List<BeerColor> findByName(String colorName) {
        JPAQuery<BeerColor> query = new JPAQuery<>(em);
        QBeerColor qColor = QBeerColor.beerColor;
        return query.from(qColor).where(qColor.name.likeIgnoreCase("%"+colorName+"%")).fetch();
    }

    public BeerColor save(BeerColor color) {
        return em.merge(color);
    }

    @Override
    public void delete(long entityId) {
        QBeerColor qColor = QBeerColor.beerColor;
        new JPADeleteClause(em, qColor).where(qColor.id.eq(entityId)).execute();
    }

    @Override
    public void deleteAll() {
        QBeerColor qColor = QBeerColor.beerColor;
        new JPADeleteClause(em, qColor).execute();
    }

    public Collection<BeerColor> findAllHavingService() {
        JPAQuery<BeerColor> query = new JPAQuery<>(em);
        QBeerColor qColor = QBeerColor.beerColor;
        QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        QBeer beer = QBeer.beer;
        return query
                .from(beer)
                .innerJoin(beer.color, qColor).fetchJoin()
                .leftJoin(beer.tap, tapBeer)
                .leftJoin(beer.bottle, bottledBeer)
                .where(bottledBeer.isNotNull().or(tapBeer.isNotNull()))
                .fetch();
    }
}
