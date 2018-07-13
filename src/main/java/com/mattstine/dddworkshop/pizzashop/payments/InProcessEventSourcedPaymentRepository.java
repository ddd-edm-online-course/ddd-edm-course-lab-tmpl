package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;

/**
 * @author Matt Stine
 */
class InProcessEventSourcedPaymentRepository extends InProcessEventSourcedRepository<PaymentRef, Payment, PaymentEvent, PaymentAddedEvent> implements PaymentRepository {
	InProcessEventSourcedPaymentRepository(EventLog eventLog,
										   Class<PaymentRef> refClass,
										   Class<Payment> aggregateClass,
										   Class<PaymentAddedEvent> addEventClass,
										   Topic topic) {
		super(eventLog, refClass, aggregateClass, addEventClass, topic);
	}
}
