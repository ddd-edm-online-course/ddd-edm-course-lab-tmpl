package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
@DisplayName("The in-process event-sourced delivery order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedDeliveryOrderRepositoryTests {

	private DeliveryOrderRepository repository;
	private EventLog eventLog;
	private DeliveryOrderRef ref;
	private DeliveryOrder deliveryOrder;

	@BeforeEach
	public void setUp() {
		eventLog = mock(EventLog.class);
		repository = new InProcessEventSourcedDeliveryOrderRepository(eventLog,
				new Topic("delivery_orders"));
		ref = repository.nextIdentity();
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
	public void should_provide_the_next_available_identity() {
		assertThat(ref.getReference()).isNotNull();
	}

	@Test
	@Tag("Lab7Tests")
	public void should_publish_an_event_when_a_delivery_order_is_added() {
		repository.add(deliveryOrder);
		DeliveryOrderAddedEvent event = new DeliveryOrderAddedEvent(ref, deliveryOrder.state());
		verify(eventLog).publish(eq(new Topic("delivery_orders")), eq(event));
	}

	@Test
	@Tag("Lab7Tests")
	public void should_hydrate_a_delivery_order_when_it_is_found_by_its_reference() {
		repository.add(deliveryOrder);

		when(eventLog.eventsBy(new Topic("delivery_orders")))
				.thenReturn(Collections.singletonList(new DeliveryOrderAddedEvent(ref, deliveryOrder.state())));

		assertThat(repository.findByRef(ref)).isEqualTo(deliveryOrder);
	}
}
