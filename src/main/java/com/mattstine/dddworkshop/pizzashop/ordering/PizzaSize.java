package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;

/**
 * @author Matt Stine
 */
public enum PizzaSize {
	MEDIUM(Amount.of(6,0));

	private Amount price;

	PizzaSize(Amount price) {
		this.price = price;
	}

	public Amount getPrice() {
		return price;
	}
}
