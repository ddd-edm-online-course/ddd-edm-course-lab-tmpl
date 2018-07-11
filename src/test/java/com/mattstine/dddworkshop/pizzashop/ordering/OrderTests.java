package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
public class OrderTests {

	private EventLog eventLog;

	@Before
	public void setUp() {
		eventLog = mock(EventLog.class);
	}

	@Test
	public void should_create_pickup_order() {
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		assertThat(order.isPickupOrder()).isTrue();
	}

	@Test
	public void should_create_delivery_order() {
		Order order = Order.withType(OrderType.DELIVERY)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		assertThat(order.isDeliveryOrder()).isTrue();
	}

	@Test
	public void can_start_builder_from_eventlog() {
		Order.withEventLog(eventLog)
				.withType(OrderType.DELIVERY)
				.withId(new OrderRef())
				.build();
	}

	@Test
	public void builder_requires_order_type() {
		assertThatIllegalStateException()
				.isThrownBy(() -> Order.withEventLog(eventLog).build());
	}

	@Test
	public void builder_requires_event_log() {
		assertThatIllegalStateException()
				.isThrownBy(() -> Order.withType(OrderType.PICKUP).build());
	}

	@Test
	public void submit_requires_at_least_one_pizza() {
		assertThatIllegalStateException()
				.isThrownBy(() -> Order.withType(OrderType.PICKUP)
						.withEventLog(eventLog)
						.build()
						.submit());
	}

	@Test
	public void submit_order_fires_event() {
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		order.addPizza(Pizza.ofSize(PizzaSize.MEDIUM).build());
		order.submit();

		verify(eventLog).publish(isA(OrderPlacedEvent.class));
	}

	@Test
	public void submit_order_updates_state() {
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		order.addPizza(Pizza.ofSize(PizzaSize.MEDIUM).build());
		order.submit();

		assertThat(order.isSubmitted()).isTrue();
	}

	@Test
	public void calculates_price() {
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		order.addPizza(Pizza.ofSize(PizzaSize.MEDIUM).build());

		assertThat(order.getPrice()).isEqualTo(PizzaSize.MEDIUM.getPrice());
	}

	@Test
	public void mark_paid_updates_state() {
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(new OrderRef())
				.build();
		order.addPizza(Pizza.ofSize(PizzaSize.MEDIUM).build());
		order.submit();
		order.markPaid();

		assertThat(order.isPaid()).isTrue();
	}

}
