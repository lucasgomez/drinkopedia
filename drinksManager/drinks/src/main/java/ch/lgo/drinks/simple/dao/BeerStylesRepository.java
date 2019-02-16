package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.QBeer;
import ch.lgo.drinks.simple.entity.QBeerStyle;
import ch.lgo.drinks.simple.entity.QBottledBeer;
import ch.lgo.drinks.simple.entity.QTapBeer;

@Repository
@Transactional
public class BeerStylesRepository implements ICrudRepository<BeerStyle>  {

    @PersistenceContext
    private EntityManager em;
    
    public BeerStyle loadById(long id) {
    	JPAQuery<BeerStyle> query = new JPAQuery<>(em);
    	QBeerStyle qStyle = QBeerStyle.beerStyle;
    	return query.from(qStyle).where(qStyle.id.eq(id)).fetchOne();
    }

    public Collection<BeerStyle> findAll() {
    	JPAQuery<BeerStyle> query = new JPAQuery<>(em);
    	QBeerStyle qStlye = QBeerStyle.beerStyle;
    	return query.from(qStlye).fetch();
    }
    
    public List<BeerStyle> findByName(String colorName) {
        JPAQuery<BeerStyle> query = new JPAQuery<>(em);
        QBeerStyle qStyle = QBeerStyle.beerStyle;
        return query.from(qStyle).where(qStyle.name.likeIgnoreCase("%"+colorName+"%")).fetch();
    }

    public BeerStyle save(BeerStyle color) {
        return em.merge(color);
    }

    @Override
    public void delete(long entityId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
        
    }

    public Collection<BeerStyle> findAllHavingService() {
        JPAQuery<BeerStyle> query = new JPAQuery<>(em);
        QBeerStyle style = QBeerStyle.beerStyle;
        QBottledBeer bottledBeer = QBottledBeer.bottledBeer;
        QTapBeer tapBeer = QTapBeer.tapBeer;
        QBeer beer = QBeer.beer;
        return query
                .distinct()
                .from(style)
                .innerJoin(beer).on(beer.style.id.eq(style.id))
                .leftJoin(beer.tap, tapBeer)
                .leftJoin(beer.bottle, bottledBeer)
                .where(bottledBeer.isNotNull().or(tapBeer.isNotNull()))
                .fetch();
    }
}
