package ch.lgo.drinks.simple.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.QBar;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;

@Repository
@Transactional
public class BarRepository {

    @PersistenceContext
    private EntityManager em;
    
    public Collection<Bar> findAll() {
    	JPAQuery<Bar> query = new JPAQuery<>(em);
    	QBar bar = QBar.bar;
    	QTapBeer tap = QTapBeer.tapBeer;
    	QBottledBeer bottle = QBottledBeer.bottledBeer;
    	return query
    			.from(bar)
    			.leftJoin(bar.tapBeers, tap).fetchJoin()
    			.leftJoin(bar.bottledBeer, bottle).fetchJoin()
    			.fetch();
    }
    
    public Bar loadTapById(long id) {
    	return loadById(id, true, false);
    }
    
    public Bar loadBottledById(long id) {
    	return loadById(id, false, true);
    }
    
    public Bar loadById(long id, boolean joinTap, boolean joinBottled) {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QBar bar = QBar.bar;
        
        query = query
        		.from(bar);
        
        if (joinTap) {
			QTapBeer tapbeer = QTapBeer.tapBeer;
			query = query
        		.leftJoin(bar.tapBeers, tapbeer).fetchJoin()
        		.leftJoin(tapbeer.beer, QBeer.beer).fetchJoin();
		}
        if (joinBottled) {
        	QBottledBeer bottledbeer = QBottledBeer.bottledBeer;
			query = query
					.leftJoin(bar.bottledBeer, bottledbeer).fetchJoin()
					.leftJoin(bottledbeer.beer, QBeer.beer).fetchJoin();
        }
        return query
        		.where(bar.id.eq(id))
        		.fetchOne();
    }

	public Bar save(Bar bottleBar) {
		return em.merge(bottleBar);
	}
}
