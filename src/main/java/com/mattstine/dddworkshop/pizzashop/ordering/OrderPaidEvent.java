package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class OrderPaidEvent implements OrderEvent {
	OrderRef ref;
}
