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
import ch.lgo.drinks.simple.entity.QBeer;

@Repository
@Transactional
public class BeersRepository implements IDrinksRepository<Beer> {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Beer loadById(long id) {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query.from(qBeer).where(qBeer.id.eq(id)).fetchOne();
    }

    @Override
    public Collection<Beer> findAll() {
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query.from(qBeer).fetch();
    }

    @Override
    public List<Beer> findByName(String beerName) {
        //TODO Something like the google search of NJ instead of exact match ignore case
        JPAQuery<Beer> query = new JPAQuery<>(em);
        QBeer qBeer = QBeer.beer;
        return query.from(qBeer).where(qBeer.name.likeIgnoreCase("%"+beerName+"%")).fetch();
    }

    @Override
    public void delete(long beerId) {
        QBeer qBeer = QBeer.beer;
        new JPADeleteClause(em, qBeer).where(qBeer.id.eq(beerId)).execute();
    }

    @Override
    public boolean exists(long beerId) {
        //TODO Useless and resources consuming -> delete or turn into exists()
        return loadById(beerId) != null;
    }

    public Beer save(Beer beerToUpdate) {
        return em.merge(beerToUpdate);
    }

    @Override
    public void deleteAll() {
        QBeer qBeer = QBeer.beer;
        new JPADeleteClause(em, qBeer).execute();
    }

}
