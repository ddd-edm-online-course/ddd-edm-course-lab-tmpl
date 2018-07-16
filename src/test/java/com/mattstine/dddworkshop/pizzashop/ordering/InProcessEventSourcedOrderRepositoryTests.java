package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
public class InProcessEventSourcedOrderRepositoryTests {

	private OrderRepository repository;
	private EventLog eventLog;
	private OrderRef ref;
	private Order order;
	private Pizza pizza;

	@Before
	public void setUp() {
		eventLog = mock(EventLog.class);
		repository = new InProcessEventSourcedOrderRepository(eventLog,
				OrderRef.class,
				Order.class,
				Order.OrderState.class,
				OrderAddedEvent.class,
				new Topic("ordering"));
		ref = repository.nextIdentity();
		order = Order.builder()
				.ref(ref)
				.type(Order.Type.PICKUP)
				.eventLog(eventLog)
				.build();
		pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
	}

	@Test
	public void provides_next_identity() {
		assertThat(ref).isNotNull();
	}

	@Test
	public void add_fires_event() {
		repository.add(order);
		OrderAddedEvent event = new OrderAddedEvent(order.getRef(), order.state());
		verify(eventLog).publish(eq(new Topic("ordering")), eq(event));
	}

	@Test
	public void find_by_ref_hydrates_added_order() {
		repository.add(order);

		when(eventLog.eventsBy(new Topic("ordering")))
				.thenReturn(Collections.singletonList(new OrderAddedEvent(ref, order.state())));

		assertThat(repository.findByRef(ref)).isEqualTo(order);
	}

	@Test
	public void find_by_ref_hydrates_order_with_added_pizza() {
		repository.add(order);
		order.addPizza(pizza);
		order.submit();

		when(eventLog.eventsBy(new Topic("ordering")))
				.thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
						new PizzaAddedEvent(ref, pizza)));

		assertThat(repository.findByRef(ref)).isEqualTo(order);
	}

	@Test
	public void find_by_ref_hydrates_submitted_order() {
		repository.add(order);
		order.addPizza(pizza);
		order.submit();

		when(eventLog.eventsBy(new Topic("ordering")))
				.thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
						new PizzaAddedEvent(ref, pizza),
						new OrderSubmittedEvent(ref)));

		assertThat(repository.findByRef(ref)).isEqualTo(order);
	}

	@Test
	public void find_by_ref_hydrates_order_with_paymentRef_assigned() {
		repository.add(order);
		order.addPizza(pizza);
		order.submit();

		PaymentRef paymentRef = new PaymentRef();
		order.assignPaymentRef(paymentRef);

		when(eventLog.eventsBy(new Topic("ordering")))
				.thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
						new PizzaAddedEvent(ref, pizza),
						new OrderSubmittedEvent(ref),
						new PaymentRefAssignedEvent(ref, paymentRef)));

		assertThat(repository.findByRef(ref)).isEqualTo(order);
	}

	@Test
	public void find_by_ref_hydrates_order_marked_paid() {
		repository.add(order);
		order.addPizza(pizza);
		order.submit();

		PaymentRef paymentRef = new PaymentRef();
		order.assignPaymentRef(paymentRef);

		order.markPaid();

		when(eventLog.eventsBy(new Topic("ordering")))
				.thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
						new PizzaAddedEvent(ref, pizza),
						new OrderSubmittedEvent(ref),
						new PaymentRefAssignedEvent(ref, paymentRef),
						new OrderPaidEvent(ref)));

		assertThat(repository.findByRef(ref)).isEqualTo(order);
	}

	@Test
	public void subscribes_to_ordering_topic() {
		verify(eventLog).subscribe(eq(new Topic("ordering")), isA(EventHandler.class));
	}

}
