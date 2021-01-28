package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrder;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderAssemblyFinishedEvent;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenService;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrder;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Matt Stine
 */
@DisplayName("The integrated default delivery service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultDeliveryServiceIntegrationTests {

	private InProcessEventLog eventLog;
	private DeliveryService deliveryService;
	private OrderingService orderingService;
	private KitchenService kitchenService;

	@BeforeEach
	public void setUp() {
		eventLog = InProcessEventLog.instance();
		DeliveryOrderRepository deliveryOrderRepository = new InProcessEventSourcedDeliveryOrderRepository(eventLog,
				new Topic("delivery_orders"));
		orderingService = mock(OrderingService.class);
		kitchenService = mock(KitchenService.class);
		deliveryService = new DeliveryService(eventLog, deliveryOrderRepository, orderingService, kitchenService);
	}

	@AfterEach
	public void tearDown() {
		this.eventLog.purgeSubscribers();
		this.eventLog.purgeEvents();
	}

	@Test
	@Tag("Lab7Tests")
	public void should_add_a_delivery_order_to_the_queue_when_it_receives_KitchenOrderAssemblyFinishedEvent() {
		KitchenOrderRef kitchenOrderRef = new KitchenOrderRef();
		KitchenOrderAssemblyFinishedEvent kitchenOrderAssemblyFinishedEvent = new KitchenOrderAssemblyFinishedEvent(kitchenOrderRef);

		OnlineOrderRef onlineOrderRef = new OnlineOrderRef();
		OnlineOrder onlineOrder = OnlineOrder.builder()
				.type(OnlineOrder.Type.DELIVERY)
				.eventLog(eventLog)
				.ref(onlineOrderRef)
				.build();

		KitchenOrder kitchenOrder = KitchenOrder.builder()
				.ref(kitchenOrderRef)
				.onlineOrderRef(onlineOrderRef)
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.LARGE).build())
				.eventLog(eventLog)
				.build();

		when(orderingService.findByRef(onlineOrderRef)).thenReturn(onlineOrder);
		when(kitchenService.findKitchenOrderByRef(kitchenOrderRef)).thenReturn(kitchenOrder);

		eventLog.publish(new Topic("kitchen_orders"), kitchenOrderAssemblyFinishedEvent);

		DeliveryOrder deliveryOrder = deliveryService.findDeliveryOrderByKitchenOrderRef(kitchenOrderRef);
		assertThat(deliveryOrder).isNotNull();
	}
}
