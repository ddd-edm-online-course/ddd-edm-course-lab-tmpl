package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class OrderAddedEvent implements OrderEvent {
	OrderRef ref;
	Order order;
}
