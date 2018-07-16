package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;

/**
 * @author Matt Stine
 */
public class InProcessEventSourcedOrderRepository extends InProcessEventSourcedRepository<OrderRef, Order, OrderEvent, OrderAddedEvent> implements OrderRepository {

	InProcessEventSourcedOrderRepository(EventLog eventLog,
										 Class<OrderRef> refClass,
										 Class<Order> aggregateClass,
										 Class<OrderAddedEvent> addEventClass,
										 Topic topic) {
		super(eventLog, refClass, aggregateClass, addEventClass, topic);
	}

	@Override
	public Order findByPaymentRef(PaymentRef paymentRef) {
		return null;
	}
}
