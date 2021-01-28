package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrder;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderPaidEvent;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.After;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("The integrated default kitchen service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultKitchenServiceIntegrationTests {

	private InProcessEventLog eventLog;
	private KitchenService kitchenService;
	private OrderingService orderingService;
	private KitchenOrderRepository kitchenOrderRepository;
	private PizzaRepository pizzaRepository;
	private KitchenOrderRef kitchenOrderRef;
	private KitchenOrder kitchenOrder;

	@BeforeEach
	public void setUp() {
		eventLog = InProcessEventLog.instance();
		kitchenOrderRepository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
				new Topic("kitchen_orders"));
		pizzaRepository = new InProcessEventSourcedPizzaRepository(eventLog,
				new Topic("pizzas"));
		orderingService = mock(OrderingService.class);
		kitchenService = new DefaultKitchenService(eventLog, kitchenOrderRepository, pizzaRepository, orderingService);
		kitchenOrderRef = kitchenOrderRepository.nextIdentity();
		kitchenOrder = KitchenOrder.builder()
				.ref(kitchenOrderRef)
				.onlineOrderRef(new OnlineOrderRef())
				.eventLog(eventLog)
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.LARGE).build())
				.build();
		kitchenOrderRepository.add(kitchenOrder);
	}

	@AfterEach
	public void tearDown() {
		this.eventLog.purgeSubscribers();
		this.eventLog.purgeEvents();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_add_a_kitchen_order_to_the_queue_when_it_receives_an_OnlineOrderPaidEvent() {
		OnlineOrderRef ref = new OnlineOrderRef();
		OnlineOrderPaidEvent orderPaidEvent = new OnlineOrderPaidEvent(ref);

		OnlineOrder onlineOrder = OnlineOrder.builder()
				.type(OnlineOrder.Type.PICKUP)
				.eventLog(eventLog)
				.ref(ref)
				.build();

		onlineOrder.addPizza(com.mattstine.dddworkshop.pizzashop.ordering.Pizza.builder()
				.size(com.mattstine.dddworkshop.pizzashop.ordering.Pizza.Size.MEDIUM)
				.build());

		when(orderingService.findByRef(eq(ref))).thenReturn(onlineOrder);

		eventLog.publish(new Topic("ordering"), orderPaidEvent);

		KitchenOrder kitchenOrder = kitchenService.findKitchenOrderByOnlineOrderRef(ref);
		assertThat(kitchenOrder).isNotNull();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_start_prepping_all_pizzas_when_it_receives_a_KitchenOrderPrepStartedEvent() {
		kitchenOrder.startPrep();

		Set<Pizza> pizzas = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);

		assertThat(pizzas.size()).isEqualTo(2);

		assertThat(pizzas.stream()
				.filter(pizza -> pizza.getSize() == Pizza.Size.MEDIUM)
				.count()).isEqualTo(1);

		assertThat(pizzas.stream()
				.filter(pizza -> pizza.getSize() == Pizza.Size.LARGE)
				.count()).isEqualTo(1);

		assertThat(pizzas.stream()
				.filter(pizza -> pizza.getState() == Pizza.State.PREPPING)
				.count()).isEqualTo(2);
	}

	@Test
	@Tag("Lab6Tests")
	public void should_start_baking_the_pizza_when_it_receives_a_PizzaPrepFinishedEvent() {
		kitchenOrder.startPrep();

		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);

		Pizza pizza = pizzasByKitchenOrderRef.stream()
				.findFirst()
				.get();

		pizza.finishPrep();

		pizza = pizzaRepository.findByRef(pizza.getRef());
		assertThat(pizza.isBaking()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_start_baking_the_order_when_it_receives_a_PizzaBakeStartedEvent() {
		kitchenOrder.startPrep();

		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);

		pizzasByKitchenOrderRef.forEach(Pizza::finishPrep);

		kitchenOrder = kitchenOrderRepository.findByRef(kitchenOrderRef);
		assertThat(kitchenOrder.isBaking()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_start_assembling_the_order_when_it_receives_the_first_PizzaBakeFinishedEvent() {
		kitchenOrder.startPrep();

		// Load pizzas that are prepping...
		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);
		pizzasByKitchenOrderRef.forEach(Pizza::finishPrep);

		// Load pizzas that are baking...
		pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);
		pizzasByKitchenOrderRef.stream()
				.findFirst()
				.ifPresent(Pizza::finishBake);

		// Ensure order has started assembly...
		kitchenOrder = kitchenOrderRepository.findByRef(kitchenOrderRef);
		assertThat(kitchenOrder.hasStartedAssembly()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_start_prepping_the_kitchen_order_when_it_receives_the_start_order_prep_command() {
		kitchenService.startOrderPrep(kitchenOrderRef);
		kitchenOrder = kitchenService.findKitchenOrderByRef(kitchenOrderRef);
		assertThat(kitchenOrder.isPrepping()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_finish_prepping_the_pizza_when_it_receives_the_finish_pizza_prep_command() {
		kitchenOrder.startPrep();

		Pizza pizza = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef).stream()
				.findFirst().get();

		PizzaRef ref = pizza.getRef();
		kitchenService.finishPizzaPrep(ref);
		pizza = kitchenService.findPizzaByRef(ref);

		assertThat(pizza.isBaking()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_remove_the_pizza_from_the_oven_when_it_receives_the_remove_pizza_from_oven_command() {
		kitchenOrder.startPrep();

		Pizza pizza = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef).stream()
				.findFirst().get();

		pizza.finishPrep();

		kitchenService.removePizzaFromOven(pizza.getRef());

		pizza = kitchenService.findPizzaByRef(pizza.getRef());

		assertThat(pizza.hasFinishedBaking()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_finish_assembling_the_order_when_it_recevies_the_final_PizzaBakeFinishedEvent() {
		kitchenOrder.startPrep();

		kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef)
				.forEach(Pizza::finishPrep);

		kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef)
				.forEach(pizza -> kitchenService.removePizzaFromOven(pizza.getRef()));

		kitchenOrder = kitchenService.findKitchenOrderByRef(kitchenOrderRef);

		assertThat(kitchenOrder.hasFinishedAssembly()).isTrue();
	}

	@Test
	@Tag("Lab6Tests")
	public void should_successfully_process_an_order_with_only_one_pizza() {
		kitchenOrderRef = kitchenOrderRepository.nextIdentity();

		kitchenOrder = KitchenOrder.builder()
				.ref(kitchenOrderRef)
				.onlineOrderRef(new OnlineOrderRef())
				.eventLog(eventLog)
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
				.build();
		kitchenOrderRepository.add(kitchenOrder);


		kitchenOrder.startPrep();

		kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef)
				.forEach(Pizza::finishPrep);

		kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef)
				.forEach(pizza -> kitchenService.removePizzaFromOven(pizza.getRef()));

		kitchenOrder = kitchenService.findKitchenOrderByRef(kitchenOrderRef);

		assertThat(kitchenOrder.hasFinishedAssembly()).isTrue();
	}

}
