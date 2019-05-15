package ch.lgo.drinks.simple.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.EntityManipulator;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.entity.Producer;
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
    
    //TODO replace usage of loadById().get() by something more meaningul
    public Optional<Beer> loadById(long id) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return Optional.ofNullable(query
        		.from(qBeer)
        		.where(qBeer.id.eq(id))
        		.fetchOne());
    }

    public Collection<Beer> findAll() {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query
        		.from(qBeer)
        		.fetch();
    }
    
    public Optional<Beer> loadByIdWithServices(long id) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
        return Optional.ofNullable(query
                .from(qBeer)
                .leftJoin(qBeer.tap, tapBeer)
                .leftJoin(qBeer.bottle, bottledBeer)
                .where(qBeer.id.eq(id))
                .fetchOne()); 
    }

    public Optional<TapBeer> loadTapByIdWithServices(long drinkId) {
        JPAQuery<TapBeer> query = new JPAQuery<>(em);
        QBeer beer = QBeer.beer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        return Optional.ofNullable(query
                .from(tapBeer)
                .join(tapBeer.beer, beer)
                .where(beer.id.eq(drinkId))
                .fetchOne());
    }
    
    public Optional<BottledBeer> loadBottleByIdWithServices(long drinkId) {
        JPAQuery<BottledBeer> query = new JPAQuery<>(em);
        QBeer beer = QBeer.beer;
        QBottledBeer bottleBeer = QBottledBeer.bottledBeer;
        return Optional.ofNullable(query
                .from(bottleBeer)
                .join(bottleBeer.beer, beer)
                .where(beer.id.eq(drinkId))
                .fetchOne());
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
	
    public List<Beer> findByName(String searchedString) {
        //TODO Something like the google search of NJ instead of exact match ignore case
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeerStyle style = QBeerStyle.beerStyle;
        QBeerColor color = QBeerColor.beerColor;
        QProducer producer = QProducer.producer;
        QPlace place = QPlace.place;
        QBeer qBeer = QBeer.beer;
        
        
        Set<BooleanExpression> subConditions = Arrays.stream(searchedString.split(" "))
                .map(subString -> 
                    qBeer.name.likeIgnoreCase("%"+subString+"%")
                    .or(color.name.likeIgnoreCase("%"+subString+"%"))
                    .or(style.name.likeIgnoreCase("%"+subString+"%"))
                    .or(producer.name.likeIgnoreCase("%"+subString+"%"))
                    .or(place.name.likeIgnoreCase("%"+subString+"%")))
                .collect(Collectors.toSet());
            
        BooleanExpression condition = null;
        for (BooleanExpression subCondition : subConditions)
            condition = condition != null ? condition.and(subCondition) : subCondition;
                
        return query
        		.from(qBeer)
        		.leftJoin(qBeer.color, color)
        		.leftJoin(qBeer.style, style)
        		.leftJoin(qBeer.producer, producer)
        		.leftJoin(producer.origin, place)
        		.where(condition)
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
    	Beer beer = loadById(bottledBeer.getBeer().getId()).get();
    	bottledBeer.setBeer(beer);
    	em.persist(bottledBeer);
		return loadById(bottledBeer.getBeer().getId()).get();
	}

	public Beer addTapBeer(TapBeer tapBeer) {
    	Beer beer = loadById(tapBeer.getBeer().getId()).get();
    	tapBeer.setBeer(beer);
		em.persist(tapBeer);
		//TODO Should return an entity where tap is fetched!
		return loadById(tapBeer.getBeer().getId()).get();
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

	public BottledBeer save(BottledBeer bottle) {
		return em.merge(bottle);
	}

	public List<Beer> findByStyle(long styleId) {
	    return findByStyle(styleId, true);
	}
	
    public List<Beer> findByStyle(long styleId, boolean havingService) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeerStyle qBeerStyle = QBeerStyle.beerStyle;
        QTapBeer tap = QTapBeer.tapBeer;
        QBottledBeer bottle = QBottledBeer.bottledBeer;
        QBeer qBeer = QBeer.beer;
        
        query = query
                .from(qBeer)
                .innerJoin(qBeer.style, qBeerStyle).fetchJoin();
        
        if (havingService)
            query = query
                .leftJoin(qBeer.tap, tap)
                .leftJoin(qBeer.bottle, bottle);
        
        BooleanExpression condition = qBeerStyle.id.eq(styleId);
        if (havingService)
            condition = condition
                .and(tap.isNotNull().or(bottle.isNotNull()));
        
        return query.where(condition)
                .orderBy(qBeer.name.asc())
                .fetch();
    }

    public List<Beer> findByColor(long colorId) {
        return findByColor(colorId, true);
    }
    
    public List<Beer> findByColor(long colorId, boolean havingService) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeerColor qBeerColor = QBeerColor.beerColor;
        QTapBeer tap = QTapBeer.tapBeer;
        QBottledBeer bottle = QBottledBeer.bottledBeer;
        QBeer qBeer = QBeer.beer;
        
        query = query
                .from(qBeer)
                .innerJoin(qBeer.color, qBeerColor).fetchJoin();
        
        if (havingService)
            query = query
                .leftJoin(qBeer.tap, tap)
                .leftJoin(qBeer.bottle, bottle);

        BooleanExpression condition = qBeerColor.id.eq(colorId);
        if (havingService)
            condition = condition
                .and(tap.isNotNull().or(bottle.isNotNull()));
        
        return query.where(condition)
                .orderBy(qBeer.name.asc())
                .fetch();
    }

    public List<Beer> findByProducer(long producerId) {
        return findByProducer(producerId, true);
    }
    
    public List<Beer> findByProducer(long producerId, boolean havingService) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        QTapBeer tap = QTapBeer.tapBeer;
        QBottledBeer bottle = QBottledBeer.bottledBeer;
        QBeer qBeer = QBeer.beer;
        query = query
                .from(qBeer)
                .innerJoin(qBeer.producer, qProducer).fetchJoin();
        
        if (havingService)
            query = query
                .leftJoin(qBeer.tap, tap)
                .leftJoin(qBeer.bottle, bottle);
        
        BooleanExpression condition = qProducer.id.eq(producerId);
        if (havingService)
            condition = condition
                .and(tap.isNotNull().or(bottle.isNotNull()));
        
        return query.where(condition)
                .orderBy(qBeer.name.asc())
                .fetch();
    }

    public List<Beer> findByOrigin(long originId) {
        return findByOrigin(originId, true);
    }
    
    public List<Beer> findByOrigin(long originId, boolean havingService) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        QPlace qPlace = QPlace.place;
        QTapBeer tap = QTapBeer.tapBeer;
        QBottledBeer bottle = QBottledBeer.bottledBeer;
        QBeer qBeer = QBeer.beer;
        query = query
                .from(qBeer)
                .innerJoin(qBeer.producer, qProducer).fetchJoin()
                .innerJoin(qProducer.origin, qPlace).fetchJoin();
        
        if (havingService)
            query = query
                .leftJoin(qBeer.tap, tap)
                .leftJoin(qBeer.bottle, bottle);
        
        BooleanExpression condition = qPlace.id.eq(originId);
        if (havingService)
            condition = condition
                .and(tap.isNotNull().or(bottle.isNotNull()));
        
        return query.where(condition)
                .orderBy(qBeer.name.asc())
                .fetch();
    }
    
    public Beer updateStyleReference(Beer beerToUpdate, Optional<Long> newId) {
        return updateReference(beerToUpdate, newId, Beer::getStyle, (beer, style) -> beer.setStyle((BeerStyle) style), BeerStyle.class);
    }
    
    public Beer updateColorReference(Beer beerToUpdate, Optional<Long> newId) {
        return updateReference(beerToUpdate, newId, Beer::getColor, (beer, color) -> beer.setColor((BeerColor) color), BeerColor.class);
    }
    
    public Beer updateProducerReference(Beer beerToUpdate, Optional<Long> newId) {
        return updateReference(beerToUpdate, newId, Beer::getProducer, (beer, producer) -> beer.setProducer((Producer) producer), Producer.class);
    }

    private Beer updateReference(Beer beer, Optional<Long> newId, Function<Beer, HasId> getter, BiFunction<Beer, HasId, Beer> setter, Class<? extends HasId> clazz) {
        try {
            return setter.apply(beer, newId
                    .map(id -> em.getReference(clazz, id))
                    .orElse(null)
            );
        } catch (EntityNotFoundException ex) {
            //Not found? Nothing then.
            return setter.apply(beer, null);
        }
    }

    public void detachThenUpdateForeignReferences(Beer beerToUpdate, Collection<EntityManipulator<? extends HasId>> manipulators) {
        //Detach all foreign entities to update
        manipulators.stream()
            .map(manipulator -> manipulator.getForeignEntityGetter().apply(beerToUpdate))
            .filter(foreignEntity -> foreignEntity != null)
            .collect(Collectors.toSet())
            .forEach(foreignEntity -> em.detach(foreignEntity));
        
        manipulators.forEach(manipulator -> manipulator.getForeignEntitySetter().apply(beerToUpdate, manipulator.getNewId()));
    }

    public void detachForeignRelations(Beer beer, Collection<Function<Beer, HasId>> foreignRelationsGetters) {
        foreignRelationsGetters.forEach(
                func -> Optional.ofNullable(func.apply(beer))
                    .ifPresent(em::detach));
    }

}
