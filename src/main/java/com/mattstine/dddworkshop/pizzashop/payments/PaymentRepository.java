package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
public interface PaymentRepository {
	PaymentRef nextIdentity();

	void add(Payment payment);

	Payment findById(PaymentRef id);
}
