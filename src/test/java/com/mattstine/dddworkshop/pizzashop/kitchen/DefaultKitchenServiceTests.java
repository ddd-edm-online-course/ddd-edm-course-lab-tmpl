package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("The default kitchen service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultKitchenServiceTests {

	private KitchenService service;
	private KitchenOrderRepository kitchenOrderRepository;
	private EventLog eventLog;

	@BeforeEach
	public void setUp() {
		eventLog = mock(EventLog.class);
		kitchenOrderRepository = mock(KitchenOrderRepository.class);
		PizzaRepository pizzaRepository = mock(PizzaRepository.class);
		OrderingService orderingService = mock(OrderingService.class);
		service = new DefaultKitchenService(eventLog, kitchenOrderRepository, pizzaRepository, orderingService);
	}

	@Test
	@Tag("Lab6Tests")
	public void should_subscribe_to_the_ordering_topic() {
		verify(eventLog).subscribe(eq(new Topic("ordering")), isA(EventHandler.class));
	}

	@Test
	@Tag("Lab6Tests")
	public void should_return_a_kitchen_order_by_its_online_order_reference() {
		OnlineOrderRef onlineOrderRef = new OnlineOrderRef();

		KitchenOrder kitchenOrder = KitchenOrder.builder()
				.eventLog(eventLog)
				.onlineOrderRef(onlineOrderRef)
				.ref(new KitchenOrderRef())
				.build();

		when(kitchenOrderRepository.findByOnlineOrderRef(eq(onlineOrderRef))).thenReturn(kitchenOrder);

		assertThat(service.findKitchenOrderByOnlineOrderRef(onlineOrderRef)).isEqualTo(kitchenOrder);
	}

	@Test
	@Tag("Lab6Tests")
	public void should_subscribe_to_the_kitchen_orders_topic() {
		verify(eventLog).subscribe(eq(new Topic("kitchen_orders")), isA(EventHandler.class));
	}

	@Test
	@Tag("Lab6Tests")
	public void should_subscribe_to_the_pizzas_topic() {
		verify(eventLog).subscribe(eq(new Topic("pizzas")), isA(EventHandler.class));
	}

}
