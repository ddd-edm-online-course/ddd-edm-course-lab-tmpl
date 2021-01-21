package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenService;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
@DisplayName("The default delivery service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultDeliveryServiceTests {

	private DeliveryService service;
	private EventLog eventLog;
	private DeliveryOrderRepository deliveryOrderRepository;

	@BeforeEach
	public void setUp() {
		eventLog = mock(EventLog.class);
		deliveryOrderRepository = mock(DeliveryOrderRepository.class);
		OrderingService orderingService = mock(OrderingService.class);
		KitchenService kitchenService = mock(KitchenService.class);
		service = new DeliveryService(eventLog, deliveryOrderRepository, orderingService, kitchenService);
	}

	@Test
	@Tag("Lab7Tests")
	public void should_subscribe_to_the_kitchen_orders_topic() {
		verify(eventLog).subscribe(eq(new Topic("kitchen_orders")), isA(EventHandler.class));
	}

	@Test
	@Tag("Lab7Tests")
	public void should_return_a_delivery_order_by_its_kitchen_order_reference() {
		KitchenOrderRef kitchenOrderRef = new KitchenOrderRef();

		DeliveryOrder deliveryOrder = DeliveryOrder.builder()
				.ref(new DeliveryOrderRef())
				.kitchenOrderRef(new KitchenOrderRef())
				.onlineOrderRef(new OnlineOrderRef())
				.pizza(DeliveryOrder.Pizza.builder().size(DeliveryOrder.Pizza.Size.MEDIUM).build())
				.eventLog(eventLog)
				.build();

		when(deliveryOrderRepository.findByKitchenOrderRef(eq(kitchenOrderRef))).thenReturn(deliveryOrder);

		assertThat(service.findDeliveryOrderByKitchenOrderRef(kitchenOrderRef)).isEqualTo(deliveryOrder);
	}
}
