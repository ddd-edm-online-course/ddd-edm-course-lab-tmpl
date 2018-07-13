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
				this.markOrderPaid(pse.getRef());
			}
		});
	}

	public OrderRef createOrder(Order.Type type) {
		OrderRef ref = repository.nextIdentity();

		Order order = Order.builder().type(type)
				.eventLog(eventLog)
				.ref(ref)
				.build();

		repository.add(order);

		return ref;
	}

	public void addPizza(OrderRef ref, Pizza pizza) {
		Order order = repository.findByRef(ref);
		order.addPizza(pizza);
	}

	public void requestPayment(OrderRef ref) {
		PaymentRef paymentRef = paymentService.createPaymentOf(Amount.of(10, 0));
		paymentService.requestPaymentFor(paymentRef);
		Order order = repository.findByRef(ref);
		order.setPaymentRef(paymentRef);
	}

	private void markOrderPaid(PaymentRef paymentRef) {
		Order order = repository.findByPaymentRef(paymentRef);
		order.markPaid();
	}
}
