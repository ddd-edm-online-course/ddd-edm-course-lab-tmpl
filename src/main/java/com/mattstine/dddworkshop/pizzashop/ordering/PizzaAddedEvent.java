package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Stine
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class PizzaAddedEvent implements Event {
	private final OrderRef orderRef;
	private final Pizza pizza;
}
