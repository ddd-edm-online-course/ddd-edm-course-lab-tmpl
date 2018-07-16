package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class PizzaAddedEvent implements OrderEvent {
	private final OrderRef ref;
	private final Pizza pizza;
}
