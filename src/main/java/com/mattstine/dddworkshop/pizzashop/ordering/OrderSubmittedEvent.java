package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class OrderSubmittedEvent implements OrderEvent {
	OrderRef ref;
}
