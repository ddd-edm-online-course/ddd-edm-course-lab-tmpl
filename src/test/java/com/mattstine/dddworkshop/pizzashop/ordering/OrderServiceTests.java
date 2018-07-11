package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentService;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
public class OrderServiceTests {

	private EventLog eventLog;
	private OrderRepository repository;
	private OrderService orderService;
	private PaymentService paymentService;

	@Before
	public void setUp() {
		eventLog = mock(EventLog.class);
		repository = mock(OrderRepository.class);
		paymentService = mock(PaymentService.class);
		orderService = new OrderService(eventLog, repository, paymentService);
	}

	@Test
	public void adds_new_order_to_repository() {
		when(repository.nextIdentity()).thenReturn(new OrderRef());
		orderService.createOrder(OrderType.PICKUP);
		verify(repository).add(isA(Order.class));
	}

	@Test
	public void returns_ref_to_new_order() {
		OrderRef ref = new OrderRef();
		when(repository.nextIdentity()).thenReturn(ref);
		OrderRef orderRef = orderService.createOrder(OrderType.PICKUP);
		assertThat(orderRef).isEqualTo(orderRef);
	}

	@Test
	public void adds_pizza_to_order() {
		OrderRef orderRef = new OrderRef();
		Order order = Order.withType(OrderType.PICKUP)
				.withEventLog(eventLog)
				.withId(orderRef)
				.build();

		when(repository.findById(orderRef)).thenReturn(order);

		Pizza pizza = Pizza.ofSize(PizzaSize.MEDIUM).build();
		orderService.addPizza(orderRef, pizza);

		assertThat(order.getPizzas()).contains(pizza);
		verify(eventLog).publish(isA(PizzaAddedEvent.class));
	}

	@Test
	public void requests_payment_for_order() {
		OrderRef orderRef = new OrderRef();
		orderService.requestPayment(orderRef);
		verify(paymentService).requestPaymentFor(orderRef, Amount.of(10, 0));
	}
}
