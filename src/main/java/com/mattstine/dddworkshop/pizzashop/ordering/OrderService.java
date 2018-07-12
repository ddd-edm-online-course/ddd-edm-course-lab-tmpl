package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentService;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentSuccessfulEvent;

/**
 * @author Matt Stine
 */
final class OrderService {
	private final EventLog eventLog;
	private final OrderRepository repository;
	private final PaymentService paymentService;

	OrderService(EventLog eventLog, OrderRepository repository, PaymentService paymentService) {
		this.eventLog = eventLog;
		this.repository = repository;
		this.paymentService = paymentService;

		eventLog.subscribe(new Topic("payments"), e -> {
			if (e instanceof PaymentSuccessfulEvent) {
				PaymentSuccessfulEvent pse = (PaymentSuccessfulEvent) e;
				this.markOrderPaid(pse.getPaymentRef());
			}
		});
	}

	public OrderRef createOrder(Order.Type type) {
		OrderRef orderRef = repository.nextIdentity();

		Order order = Order.builder().type(type)
				.eventLog(eventLog)
				.ref(orderRef)
				.build();

		repository.add(order);

		return orderRef;
	}

	public void addPizza(OrderRef orderRef, Pizza pizza) {
		Order order = repository.findById(orderRef);
		order.addPizza(pizza);
		eventLog.publish(new Topic("ordering"), new PizzaAddedEvent(orderRef, pizza));
	}

	public void requestPayment(OrderRef orderRef) {
		PaymentRef paymentRef = paymentService.createPaymentOf(Amount.of(10, 0));
		paymentService.requestPaymentFor(paymentRef);
		Order order = repository.findById(orderRef);
		order.setPaymentRef(paymentRef);
	}

	private void markOrderPaid(PaymentRef paymentRef) {
		Order order = repository.findByPaymentRef(paymentRef);
		order.markPaid();
	}
}
