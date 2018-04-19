package ch.lgo.drinks.simple.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.QBar;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;
import ch.lgo.drinks.simple.entity.TapBeer;

@Repository
@Transactional
public class BarRepository {

    @PersistenceContext
    private EntityManager em;

    public Collection<Bar> findAll() {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QBar bar = QBar.bar;
        return query
                .from(bar)
                .fetch();
    }
    
    public Collection<Bar> findAllWithBeers() {
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
    
    public List<Beer> findBeersByBar(long barId) {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QTapBeer tap = QTapBeer.tapBeer;
        QBottledBeer bottle = QBottledBeer.bottledBeer;
        QBeer beer = QBeer.beer;
        QBar bar = QBar.bar;
        Bar found = query
                .from(bar)
                .leftJoin(bar.tapBeers, tap).fetchJoin()
                .leftJoin(bar.bottledBeer, bottle).fetchJoin()
                .leftJoin(tap.beer, beer).fetchJoin()
                .leftJoin(bottle.beer, beer).fetchJoin()
                .where(bar.id.eq(barId))
                .fetchOne();
        Set<Beer> beers = found.getBottledBeer().stream()
                .map(BottledBeer::getBeer)
                .collect(Collectors.toSet());
        beers.addAll(found.getTapBeers().stream()
                .map(TapBeer::getBeer)
                .collect(Collectors.toSet()));
        List<Beer> list = new ArrayList<Beer>(beers);
        list.sort(Beer.byName);
        return list;
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
