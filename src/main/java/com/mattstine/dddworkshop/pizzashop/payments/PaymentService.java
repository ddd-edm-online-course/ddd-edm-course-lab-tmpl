package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
/**
 * @author Matt Stine
 */
public class PaymentService {
	private final PaymentProcessor processor;
	private final PaymentRepository repository;
	private final EventLog eventLog;

	public PaymentService(PaymentProcessor processor, PaymentRepository repository, EventLog eventLog) {
		this.processor = processor;
		this.repository = repository;
		this.eventLog = eventLog;
	}

	public PaymentRef requestPaymentFor(OrderRef orderRef, Amount amount) {
		PaymentRef ref = repository.nextIdentity();

		Payment payment = Payment.of(amount)
				.withId(ref)
				.withProcessor(processor)
				.build();

		payment.request();
		repository.add(payment);

		return ref;
	}
}
