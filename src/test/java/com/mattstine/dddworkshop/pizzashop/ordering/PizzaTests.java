package com.mattstine.dddworkshop.pizzashop.ordering;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
public class PizzaTests {

	@Test
	public void build_with_size() {
		Pizza pizza = Pizza.ofSize(PizzaSize.MEDIUM).build();
		assertThat(pizza.getSize()).isEqualTo(PizzaSize.MEDIUM);
	}

	@Test
	public void calculates_price() {
		Pizza pizza = Pizza.ofSize(PizzaSize.MEDIUM).build();
		assertThat(pizza.getPrice()).isEqualTo(PizzaSize.MEDIUM.getPrice());
	}
}
