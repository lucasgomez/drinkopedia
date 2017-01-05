package ch.lgo.drinks.simple.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.NonAlcoolicBeverage;
import ch.lgo.drinks.simple.entity.QNonAlcoolicBeverage;

@Repository
@Transactional
public class NonAlcoolicBeverageRepository implements IDrinksRepository<NonAlcoolicBeverage> {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public NonAlcoolicBeverage loadById(long id) {
        JPAQuery<NonAlcoolicBeverage> query = new JPAQuery<>(em);
        QNonAlcoolicBeverage qNAB = QNonAlcoolicBeverage.nonAlcoolicBeverage;
        return query.from(qNAB).where(qNAB.id.eq(id)).fetchOne();
    }

    @Override
    public Collection<NonAlcoolicBeverage> findAll() {
        JPAQuery<NonAlcoolicBeverage> query = new JPAQuery<>(em);
        QNonAlcoolicBeverage qNABonAlcoolicBeverage = QNonAlcoolicBeverage.nonAlcoolicBeverage;
        return query.from(qNABonAlcoolicBeverage).fetch();
    }

    @Override
    public Collection<NonAlcoolicBeverage> findByName(String nabName) {
        //TODO Something like the google search of NJ instead of exact match ignore case
        JPAQuery<NonAlcoolicBeverage> query = new JPAQuery<>(em);
        QNonAlcoolicBeverage qNAB = QNonAlcoolicBeverage.nonAlcoolicBeverage;
        return query.from(qNAB).where(qNAB.name.likeIgnoreCase("%"+nabName+"%")).fetch();
    }

    @Override
    public void delete(long nabId) {
        QNonAlcoolicBeverage qNAB = QNonAlcoolicBeverage.nonAlcoolicBeverage;
        new JPADeleteClause(em, qNAB).where(qNAB.id.eq(nabId)).execute();
    }

    @Override
    public boolean exists(long nabId) {
        //TODO Useless and resources consuming -> delete or turn into exists()
        return loadById(nabId) != null;
    }

    public NonAlcoolicBeverage save(NonAlcoolicBeverage nabToUpdate) {
        return em.merge(nabToUpdate);
    }

    @Override
    public void deleteAll() {
        QNonAlcoolicBeverage qNonAlcoolicBeverage = QNonAlcoolicBeverage.nonAlcoolicBeverage;
        new JPADeleteClause(em, qNonAlcoolicBeverage).execute();
    }

}
