package ch.lgo.drinks.simple.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import ch.lgo.drinks.simple.entity.DrinkType;
import ch.lgo.drinks.simple.entity.QDrinkType;

@Repository
@Transactional
public class DrinkTypeRepository implements IDrinkTypeRepository {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<DrinkType> findByName(String drinkTypeName) {
		JPAQuery<DrinkType> query = new JPAQuery<>(em);
		QDrinkType qDrinkType = QDrinkType.drinkType;
		return query.from(qDrinkType).where(qDrinkType.name.likeIgnoreCase("%"+drinkTypeName+"%")).fetch();
	}

	@Override
	public DrinkType save(DrinkType drinkType) {
		return em.merge(drinkType);
	}

	@Override
	public void deleteAll() {
		QDrinkType qDrinkType = QDrinkType.drinkType;
		new JPADeleteClause(em, qDrinkType).execute();
	}

	@Override
	public DrinkType loadByName(String drinkTypeName) {
		JPAQuery<DrinkType> query = new JPAQuery<>(em);
		QDrinkType qDrinkType = QDrinkType.drinkType;
		return query.from(qDrinkType).where(qDrinkType.name.likeIgnoreCase(drinkTypeName)).fetchOne();
	}

}
