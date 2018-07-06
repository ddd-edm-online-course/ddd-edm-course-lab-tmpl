package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
public interface PaymentProcessor {
	void request(Payment payment);
}
