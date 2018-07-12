package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
interface PaymentProcessor {
	void request(Payment payment);
}
