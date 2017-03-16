package ch.lgo.drinks.simple.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.entity.QProducer;

@Repository
@Transactional
public class ProducerRepository implements ICrudRepository<Producer> {
	
	@PersistenceContext
	private EntityManager em;
	
    public Producer loadById(long id) {
        JPAQuery<Producer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        return query.from(qProducer).where(qProducer.id.eq(id)).fetchOne();
    }

    public Collection<Producer> findAll() {
        JPAQuery<Producer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        return query.from(qProducer).fetch();
    }

    public List<Producer> findByName(String producerName) {
        //TODO Something like the google search of NJ instead of exact match ignore case
        JPAQuery<Producer> query = new JPAQuery<>(em);
        QProducer qProducer = QProducer.producer;
        return query.from(qProducer).where(qProducer.name.likeIgnoreCase("%"+producerName+"%")).fetch();
    }

    public void delete(long producerId) {
        QProducer qProducer = QProducer.producer;
        new JPADeleteClause(em, qProducer).where(qProducer.id.eq(producerId)).execute();
    }

    public Producer save(Producer producerToUpdate) {
        return em.merge(producerToUpdate);
    }

    public void deleteAll() {
        QProducer qProducer = QProducer.producer;
        new JPADeleteClause(em, qProducer).execute();
    }
}

