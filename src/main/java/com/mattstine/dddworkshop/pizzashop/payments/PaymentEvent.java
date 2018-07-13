package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
interface PaymentEvent {
	PaymentRef getRef();
}
