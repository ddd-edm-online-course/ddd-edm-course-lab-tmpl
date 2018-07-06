package com.mattstine.dddworkshop.pizzashop.infrastructure;

import com.mattstine.dddworkshop.pizzashop.ordering.OrderPlacedEvent; /**
 * @author Matt Stine
 */
public interface EventLog {
	void publish(OrderPlacedEvent event);
}
