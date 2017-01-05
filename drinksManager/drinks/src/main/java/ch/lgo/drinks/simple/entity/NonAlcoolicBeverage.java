package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class NonAlcoolicBeverage extends Drink {

    @Override
    @Transient
    public DrinkTypeEnum getDrinkType() {
        return DrinkTypeEnum.NON_ALCOOLIC_BEVERAGE;
    }

}
