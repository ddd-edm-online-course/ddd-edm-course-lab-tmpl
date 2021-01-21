package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Matt Stine
 */
@DisplayName("A delivery order")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DeliveryOrderTests {

	private DeliveryOrder deliveryOrder;
	private DeliveryOrderRef ref;

	@BeforeEach
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
	@Tag("Lab7Tests")
	public void should_be_buildable() {
		assertThat(deliveryOrder).isNotNull();
	}

	@Test
	@Tag("Lab7Tests")
	public void should_start_in_the_ready_for_delivery_state() {
		assertThat(deliveryOrder.isReadyForDelivery()).isTrue();
	}

	@Test
	@Tag("Lab7Tests")
	public void accumulator_function_should_return_an_added_delivery_order() {
		DeliveryOrderAddedEvent deliveryOrderAddedEvent = new DeliveryOrderAddedEvent(ref, deliveryOrder.state());
		assertThat(deliveryOrder.accumulatorFunction().apply(deliveryOrder.identity(), deliveryOrderAddedEvent)).isEqualTo(deliveryOrder);
	}
}
