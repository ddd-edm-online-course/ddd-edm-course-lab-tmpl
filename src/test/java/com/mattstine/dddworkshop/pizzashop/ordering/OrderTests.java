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
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		assertThat(order.isPickupOrder()).isTrue();
	}

	@Test
	public void should_create_delivery_order() {
		Order order = Order.builder()
				.type(Order.Type.DELIVERY)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		assertThat(order.isDeliveryOrder()).isTrue();
	}

	@Test
	public void submit_requires_at_least_one_pizza() {
		assertThatIllegalStateException()
				.isThrownBy(() -> Order.builder().type(Order.Type.PICKUP)
						.ref(new OrderRef())
						.eventLog(eventLog)
						.build()
						.submit());
	}

	@Test
	public void should_add_pizza() {
		Pizza pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();

		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();

		order.addPizza(pizza);

		assertThat(order.getPizzas()).contains(pizza);
	}

	@Test
	public void adding_pizza_fires_event() {
		Pizza pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();

		OrderRef ref = new OrderRef();
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(ref)
				.build();

		order.addPizza(pizza);

		verify(eventLog).publish(new PizzaAddedEvent(ref, pizza));
	}

	@Test
	public void submit_order_fires_event() {
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
		order.submit();

		verify(eventLog).publish(isA(OrderPlacedEvent.class));
	}

	@Test
	public void submit_order_updates_state() {
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
		order.submit();

		assertThat(order.isSubmitted()).isTrue();
	}

	@Test
	public void calculates_price() {
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());

		assertThat(order.calculatePrice()).isEqualTo(Pizza.Size.MEDIUM.getPrice());
	}

	@Test
	public void mark_paid_updates_state() {
		Order order = Order.builder()
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.ref(new OrderRef())
				.build();
		order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
		order.submit();
		order.markPaid();

		assertThat(order.isPaid()).isTrue();
	}

}
