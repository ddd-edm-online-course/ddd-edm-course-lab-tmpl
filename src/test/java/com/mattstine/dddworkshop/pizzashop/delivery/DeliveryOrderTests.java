package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.lab.infrastructure.Lab7Tests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Matt Stine
 */
public class DeliveryOrderTests {

	private DeliveryOrder deliveryOrder;
	private DeliveryOrderRef ref;

	@Before
	public void setUp() {
		EventLog eventLog = mock(EventLog.class);
		ref = new DeliveryOrderRef();
		deliveryOrder = DeliveryOrder.builder()
				.ref(ref)
				.kitchenOrderRef(new KitchenOrderRef())
				.onlineOrderRef(new OnlineOrderRef())
				.pizza(DeliveryOrder.Pizza.builder().size(DeliveryOrder.Pizza.Size.MEDIUM).build())
				.eventLog(eventLog)
				.build();
	}

	@Test
	@Category(Lab7Tests.class)
	public void can_build_new_order() {
		assertThat(deliveryOrder).isNotNull();
	}

	@Test
	@Category(Lab7Tests.class)
	public void new_order_is_ready_for_delivery() {
		assertThat(deliveryOrder.isReadyForDelivery()).isTrue();
	}

	@Test
	@Category(Lab7Tests.class)
	public void accumulator_apply_with_orderAddedEvent_returns_order() {
		DeliveryOrderAddedEvent deliveryOrderAddedEvent = new DeliveryOrderAddedEvent(ref, deliveryOrder.state());
		assertThat(deliveryOrder.accumulatorFunction().apply(deliveryOrder.identity(), deliveryOrderAddedEvent)).isEqualTo(deliveryOrder);
	}
}
