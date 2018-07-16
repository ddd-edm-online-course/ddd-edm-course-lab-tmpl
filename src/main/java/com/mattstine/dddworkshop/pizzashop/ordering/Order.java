package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import lombok.*;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
@Value
@NoArgsConstructor //TODO: Smelly...can I do reflection without this?
public class Order implements Aggregate {
	@NonFinal
	Type type;
	@NonFinal
	EventLog eventLog;
	@NonFinal
	OrderRef ref;
	@NonFinal
	State state;
	@NonFinal
	List<Pizza> pizzas;
	@NonFinal
	PaymentRef paymentRef;

	@Builder
	private Order(@NonNull Type type, @NonNull EventLog eventLog, @NonNull OrderRef ref) {
		this.type = type;
		this.eventLog = eventLog;
		this.ref = ref;
		this.pizzas = new ArrayList<>();

		this.state = State.NEW;
	}

	public boolean isPickupOrder() {
		return this.type == Type.PICKUP;
	}

	public boolean isDeliveryOrder() {
		return this.type == Type.DELIVERY;
	}

	public boolean isNew() {
		return state == State.NEW;
	}

	public boolean isSubmitted() {
		return this.state == State.SUBMITTED;
	}

	public boolean isPaid() {
		return state == State.PAID;
	}

	public void addPizza(Pizza pizza) {
		if (this.state != State.NEW) {
			throw new IllegalStateException("Can only add Pizza to NEW Order");
		}

		this.pizzas.add(pizza);
		eventLog.publish(new Topic("ordering"), new PizzaAddedEvent(ref, pizza));
	}

	public void submit() {
		if (this.state != State.NEW) {
			throw new IllegalStateException("Can only submit NEW Order");
		}

		if (this.pizzas.isEmpty()) {
			throw new IllegalStateException("Cannot submit Order without at least one Pizza");
		}

		this.state = State.SUBMITTED;
		eventLog.publish(new Topic("ordering"), new OrderSubmittedEvent(ref));
	}

	public void assignPaymentRef(PaymentRef paymentRef) {
		this.paymentRef = paymentRef;
		eventLog.publish(new Topic("ordering"), new PaymentRefAssignedEvent(ref, paymentRef));
	}

	public Amount calculatePrice() {
		return this.pizzas.stream()
				.map(Pizza::calculatePrice)
				.reduce(Amount.of(0,0), Amount::plus);
	}

	public void markPaid() {
		if (this.state != State.SUBMITTED) {
			throw new IllegalStateException("Can only mark SUBMITTED Order as Paid");
		}

		this.state = State.PAID;
		eventLog.publish(new Topic("ordering"), new OrderPaidEvent(ref));
	}

	@Override
	public Order identity() {
		return Order.builder()
				.eventLog(EventLog.IDENTITY)
				.ref(OrderRef.IDENTITY)
				.type(Type.IDENTITY)
				.build();
	}

	@Override
	public BiFunction<Order, OrderEvent, Order> accumulatorFunction() {
		return new Accumulator();
	}

	enum State {
		NEW, SUBMITTED, PAID
	}

	enum Type {
		IDENTITY, DELIVERY, PICKUP
	}

	static class Accumulator implements BiFunction<Order, OrderEvent, Order> {

		@Override
		public Order apply(Order order, OrderEvent orderEvent) {
			if (orderEvent instanceof OrderAddedEvent) {
				OrderAddedEvent oae = (OrderAddedEvent) orderEvent;
				return oae.getOrder();
			} else if (orderEvent instanceof PizzaAddedEvent) {
				PizzaAddedEvent pae = (PizzaAddedEvent) orderEvent;
				order.pizzas.add(pae.getPizza());
				return order;
			} else if (orderEvent instanceof OrderSubmittedEvent) {
				order.state = State.SUBMITTED;
				return order;
			} else if (orderEvent instanceof PaymentRefAssignedEvent) {
				PaymentRefAssignedEvent prae = (PaymentRefAssignedEvent) orderEvent;
				order.paymentRef = prae.getPaymentRef();
				return order;
			} else if (orderEvent instanceof OrderPaidEvent) {
				order.state = State.PAID;
				return order;
			}
			throw new IllegalStateException("Unknown OrderEvent");
		}
	}
}
