package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;

/**
 * @author Matt Stine
 */
class InProcessEventSourcedPaymentRepository implements PaymentRepository {
	private final EventLog eventLog;

	InProcessEventSourcedPaymentRepository(EventLog eventLog) {
		this.eventLog = eventLog;
	}

	@Override
	public PaymentRef nextIdentity() {
		return new PaymentRef();
	}

	@Override
	public void add(Payment payment) {
		eventLog.publish(new Topic("payments"),
				new PaymentAddedEvent(payment.getRef(), payment));
	}

	@Override
	public Payment findByRef(PaymentRef ref) {
		return eventLog.eventsBy(new Topic("payments"))
				.stream()
				.map(e -> (PaymentEvent) e)
				.filter(e -> ref.equals(e.getRef()))
				.reduce(Payment.IDENTITY,
						Payment.ACCUMULATOR,
						(a, b) -> null);
	}
}
