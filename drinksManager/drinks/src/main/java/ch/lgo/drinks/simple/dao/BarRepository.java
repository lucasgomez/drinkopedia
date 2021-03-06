package ch.lgo.drinks.simple.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.HasBar;
import ch.lgo.drinks.simple.entity.QBar;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;
import ch.lgo.drinks.simple.entity.TapBeer;

@Repository
@Component
@Transactional
public class BarRepository implements ICrudRepository<Bar> {

    @PersistenceContext
    private EntityManager em;

    public Collection<Bar> findAll() {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QBar bar = QBar.bar;
        return query
                .from(bar)
                .fetch();
    }
    
    public Collection<Bar> findAllHavingService() {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QBar bar = QBar.bar;
        QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        
        return query
                .from(bar).distinct()
                .leftJoin(bar.bottledBeer, bottledBeer)
                .leftJoin(bar.tapBeers, tapBeer)
                .where(bottledBeer.isNotNull().or(tapBeer.isNotNull()))
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
    
    public Optional<Bar> loadTapById(long id) {
    	return loadById(id, true, false);
    }
    
    public Optional<Bar> loadBottledById(long id) {
    	return loadById(id, false, true);
    }
    
    public Optional<Bar> loadById(long id, boolean joinTap, boolean joinBottled) {
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
        return Optional.ofNullable(
                query
        		.where(bar.id.eq(id))
        		.fetchOne());
    }

	public Bar save(Bar bottleBar) {
		return em.merge(bottleBar);
	}
	
	private void associateBeerToBarsAndSave(Beer beer, Set<Long> idBarsAdded, Set<Long> idBarsRemoved,
	        BiFunction<Bar, HasBar<?>, Bar> serviceAdder, BiFunction<Bar, HasBar<?>, Bar> serviceRemover,
	        Function<Beer, HasBar<?>> servingMethodGetter) {
	    
	    //Remove beer from unassociated bars
	    idBarsRemoved.stream()
            .map(barId -> loadById(barId, true, true))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(bar -> serviceRemover.apply(bar, servingMethodGetter.apply(beer)))
            .forEach(this::save);
        
	    //Add beer to newly associated bars
        idBarsAdded.stream()
            .map(barId -> loadById(barId, true, true))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(bar -> serviceAdder.apply(bar, servingMethodGetter.apply(beer)))
            .forEach(this::save);
	}
	
	public void updateBeerToBarsAssociationsAndSave(Beer beer, Set<Long> idBarsAddedForTap, Set<Long> idBarsRemovedForTap, Set<Long> idBarsAddedForBottle, Set<Long> idBarsRemovedForBottle) {
	    associateBeerToBarsAndSave(beer, idBarsAddedForBottle, idBarsRemovedForBottle, 
                (bar, bottledBeer) -> bar.addBottledBeer((BottledBeer) bottledBeer), (bar, bottledBeer) -> bar.removeBottledBeer((BottledBeer) bottledBeer),
                Beer::getBottle);
	    associateBeerToBarsAndSave(beer, idBarsAddedForTap, idBarsRemovedForTap, 
               (bar, tapBeer) -> bar.addTapBeer((TapBeer) tapBeer), (bar, tapBeer) -> bar.removeTapBeer((TapBeer) tapBeer),
               Beer::getTap);

	    em.flush();
	}

    @Override
    public Optional<Bar> loadById(long id) {
        return loadById(id, false, false);
    }

    @Override
    public List<Bar> findByName(String entityName) {
        JPAQuery<Bar> query = new JPAQuery<>(em);
        QBar qBar = QBar.bar;
        return query.from(qBar).where(qBar.name.likeIgnoreCase("%"+entityName+"%")).fetch();
    }

    @Override
    public void delete(long entityId) {
        QBar qBar = QBar.bar;
        new JPADeleteClause(em, qBar).where(qBar.id.eq(entityId)).execute();
    }

    @Override
    public void deleteAll() {
        QBar qBar = QBar.bar;
        new JPADeleteClause(em, qBar).execute();
        
    }
    
    public Bar getBarReference(long id) {
        return em.getReference(Bar.class, id);
    }
}
