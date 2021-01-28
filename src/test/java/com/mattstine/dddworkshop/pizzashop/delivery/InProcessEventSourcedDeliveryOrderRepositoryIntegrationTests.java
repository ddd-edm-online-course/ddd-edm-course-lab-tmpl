package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
@DisplayName("The integrated in-process event-sourced delivery order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedDeliveryOrderRepositoryIntegrationTests {

	private DeliveryOrderRepository repository;
	private InProcessEventLog eventLog;
	private DeliveryOrder deliveryOrder;
	private KitchenOrderRef kitchenOrderRef;

	@BeforeEach
	public void setUp() {
		eventLog = InProcessEventLog.instance();
		repository = new InProcessEventSourcedDeliveryOrderRepository(eventLog,
				new Topic("delivery_orders"));
		DeliveryOrderRef ref = repository.nextIdentity();
		kitchenOrderRef = new KitchenOrderRef();
		deliveryOrder = DeliveryOrder.builder()
				.ref(ref)
				.kitchenOrderRef(kitchenOrderRef)
				.onlineOrderRef(new OnlineOrderRef())
				.pizza(DeliveryOrder.Pizza.builder().size(DeliveryOrder.Pizza.Size.MEDIUM).build())
				.eventLog(eventLog)
				.build();
	}

	@AfterEach
	public void tearDown() {
		this.eventLog.purgeSubscribers();
		this.eventLog.purgeEvents();
	}

	@Test
	@Tag("Lab7Tests")
	public void should_hydrate_a_delivery_order_when_found_by_its_kitchen_order_reference() {
		repository.add(deliveryOrder);

		assertThat(repository.findByKitchenOrderRef(kitchenOrderRef)).isEqualTo(deliveryOrder);
	}
}
