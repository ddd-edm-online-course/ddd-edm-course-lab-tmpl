package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.AggregateEvent;

/**
 * @author Matt Stine
 */
interface PaymentEvent extends AggregateEvent {
	PaymentRef getRef();
}
