package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.payments.Payment;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentProcessor;

/**
 * @author Matt Stine
 */
public class OrderService {
	private final EventLog eventLog;
	private final OrderRepository repository;
	private final PaymentProcessor paymentProcessor;

	OrderService(EventLog eventLog, OrderRepository repository, PaymentProcessor paymentProcessor) {
		this.eventLog = eventLog;
		this.repository = repository;
		this.paymentProcessor = paymentProcessor;
	}

	public OrderRef createOrder(OrderType type) {
		OrderRef orderRef = repository.nextIdentity();

		Order order = Order.withType(type)
				.withEventLog(eventLog)
				.withId(orderRef)
				.build();

		repository.add(order);

		return orderRef;
	}

	public void addPizza(OrderRef orderRef, Pizza pizza) {
		Order order = repository.findById(orderRef);
		order.addPizza(pizza);
		eventLog.publish(new PizzaAddedEvent(orderRef, pizza));
	}

	public void requestPayment(OrderRef orderRef) {
		Order order = repository.findById(orderRef);
		Payment payment = Payment.of(order.getPrice())
				.withProcessor(paymentProcessor)
				.build();
		payment.request();
	}
}
