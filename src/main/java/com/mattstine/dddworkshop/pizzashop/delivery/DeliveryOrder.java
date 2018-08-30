package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.AggregateState;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
@Value
public final class DeliveryOrder implements Aggregate {

	@Builder
	private DeliveryOrder(@NonNull DeliveryOrderRef ref,
						  @NonNull KitchenOrderRef kitchenOrderRef,
						  @NonNull OnlineOrderRef onlineOrderRef,
						  @Singular List<Pizza> pizzas,
						  @NonNull EventLog eventLog) {
		throw new IllegalStateException("Builder constructor must be implemented!");
	}

	/**
	 * Private no-args ctor to support reflection ONLY.
	 */
	@SuppressWarnings("unused")
	private DeliveryOrder() {
	}

	@Override
	public DeliveryOrder identity() {
		return null;
	}

	@Override
	public BiFunction<DeliveryOrder, DeliveryOrderEvent, DeliveryOrder> accumulatorFunction() {
		return null;
	}

	@Override
	public Ref getRef() {
		return null;
	}

	@Override
	public OrderState state() {
		return null;
	}

	boolean isReadyForDelivery() {
		return false;
	}

	enum State {
	}

	private static class Accumulator implements BiFunction<DeliveryOrder, DeliveryOrderEvent, DeliveryOrder> {

		@Override
		public DeliveryOrder apply(DeliveryOrder deliveryOrder, DeliveryOrderEvent deliveryOrderEvent) {
			return null;
		}
	}

	/*
	 * Pizza Value Object for KitchenOrder Details Only
	 */
	@Value
	public static final class Pizza {
		Size size;

		@Builder
		private Pizza(@NonNull Size size) {
			this.size = size;
		}

		public enum Size {
			SMALL, MEDIUM, LARGE
		}
	}

	@Value
	static class OrderState implements AggregateState {
	}
}
