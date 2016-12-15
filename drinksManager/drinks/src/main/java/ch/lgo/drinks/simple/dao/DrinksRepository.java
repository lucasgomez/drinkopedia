package ch.lgo.drinks.simple.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.entity.QDrink;

@Repository
@Transactional
public class DrinksRepository implements IDrinkRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Drink> findByType(DrinkTypeEnum type) {
		JPAQuery<Drink> query = new JPAQuery<>(em);
		QDrink qDrink = QDrink.drink;
		return query.from(qDrink).where(qDrink.type.eq(type)).fetch();
	}

	@Override
	public Drink loadById(long id) {
		JPAQuery<Drink> query = new JPAQuery<>(em);
		QDrink qDrink = QDrink.drink;
		return query.from(qDrink).where(qDrink.id.eq(id)).fetchOne();
	}

	@Override
	public Iterable<Drink> findAll() {
		JPAQuery<Drink> query = new JPAQuery<>(em);
		QDrink qDrink = QDrink.drink;
		return query.from(qDrink).fetch();
	}

	@Override
	public List<Drink> findByName(String drinkName) {
		//TODO Something like the google search of NJ instead of exact match ignore case
		JPAQuery<Drink> query = new JPAQuery<>(em);
		QDrink qDrink = QDrink.drink;
		return query.from(qDrink).where(qDrink.name.likeIgnoreCase("%"+drinkName+"%")).fetch();
	}

	@Override
	public boolean exists(long drinkId) {
		//TODO Useless and resources consuming -> delete or turn into exists()
		return loadById(drinkId) != null;
	}

	@Override
	public Drink save(Drink drinkToUpdate) {
		return em.merge(drinkToUpdate);
	}

	@Override
	public void delete(long drinkId) {
		QDrink qDrink = QDrink.drink;
		new JPADeleteClause(em, qDrink).where(qDrink.id.eq(drinkId)).execute();
	}

	@Override
	public void deleteAll() {
		QDrink qDrink = QDrink.drink;
		new JPADeleteClause(em, qDrink).execute();
	}

}
