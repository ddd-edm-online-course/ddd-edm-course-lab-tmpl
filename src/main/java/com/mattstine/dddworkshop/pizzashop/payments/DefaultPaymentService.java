package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;

/**
 * @author Matt Stine
 */
final class DefaultPaymentService implements PaymentService {
	private final PaymentProcessor processor;
	private final PaymentRepository repository;
	private final EventLog eventLog;

	DefaultPaymentService(PaymentProcessor processor, PaymentRepository repository, EventLog eventLog) {
		this.processor = processor;
		this.repository = repository;
		this.eventLog = eventLog;
	}

	@Override
	public PaymentRef requestPaymentFor(Amount amount) {
		PaymentRef ref = repository.nextIdentity();

		Payment payment = Payment.builder()
				.amount(amount)
				.ref(ref)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();
		repository.add(payment);

		payment.request();

		return ref;
	}

	@Override
	public void receivePaymentProcessedEvent(PaymentProcessedEvent ppEvent) {
		Payment payment = repository.findById(ppEvent.getRef());

		if (ppEvent.isSuccessful()) {
			payment.markSuccessful();
		} else if (ppEvent.isFailed()) {
			payment.markFailed();
		}
	}
}
