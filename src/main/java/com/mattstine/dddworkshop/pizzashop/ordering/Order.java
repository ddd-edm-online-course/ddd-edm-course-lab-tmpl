package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import lombok.*;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matt Stine
 */
@Value
public class Order {
	Type type;
	EventLog eventLog;
	OrderRef ref;
	@NonFinal
	State state;
	List<Pizza> pizzas;
	@NonFinal
	@Setter
	PaymentRef paymentRef;

	@Builder
	private Order(@NonNull Type type, @NonNull EventLog eventLog, @NonNull OrderRef ref) {
		this.type = type;
		this.eventLog = eventLog;
		this.ref = ref;
		this.pizzas = new ArrayList<>();
	}

	public boolean isPickupOrder() {
		return this.type == Type.PICKUP;
	}

	public boolean isDeliveryOrder() {
		return this.type == Type.DELIVERY;
	}

	public boolean isSubmitted() {
		return this.state == State.SUBMITTED;
	}

	public void addPizza(Pizza pizza) {
		this.pizzas.add(pizza);
		eventLog.publish(new PizzaAddedEvent(ref, pizza));
	}

	public void submit() {
		if (this.pizzas.isEmpty()) {
			throw new IllegalStateException("Cannot submit Order without at least one Pizza");
		}

		this.state = State.SUBMITTED;
		eventLog.publish(new OrderPlacedEvent());
	}

	public Amount calculatePrice() {
		return this.pizzas.stream()
				.map(Pizza::calculatePrice)
				.reduce(Amount.of(0,0), Amount::plus);
	}

	public boolean isPaid() {
		return state == State.PAID;
	}

	public void markPaid() {
		this.state = State.PAID;
		eventLog.publish(new OrderPaidEvent());
	}

	public enum State {
		PAID, SUBMITTED
	}

	public enum Type {
		DELIVERY, PICKUP
	}
}
