package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class PizzaAddedEvent implements Event {
	private final OrderRef orderRef;
	private final Pizza pizza;
}
