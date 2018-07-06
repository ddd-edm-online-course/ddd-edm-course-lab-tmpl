package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;

/**
 * @author Matt Stine
 */
class PizzaAddedEvent implements Event {
	private final OrderRef orderRef;
	private final Pizza pizza;

	PizzaAddedEvent(OrderRef orderRef, Pizza pizza) {
		this.orderRef = orderRef;
		this.pizza = pizza;
	}
}
