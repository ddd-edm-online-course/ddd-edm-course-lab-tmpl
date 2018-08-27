package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrder;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderPaidEvent;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KitchenServiceIntegrationTests {

    private InProcessEventLog eventLog;
    private KitchenService kitchenService;
    private OrderingService orderingService;
    private KitchenOrderRepository kitchenOrderRepository;
	private PizzaRepository pizzaRepository;
	private KitchenOrderRef kitchenOrderRef;
	private KitchenOrder kitchenOrder;

    @Before
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        kitchenOrderRepository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
                new Topic("kitchen_orders"));
		pizzaRepository = new InProcessEventSourcedPizzaRepository(eventLog,
                new Topic("pizzas"));
        orderingService = mock(OrderingService.class);
        kitchenService = new KitchenService(eventLog, kitchenOrderRepository, pizzaRepository, orderingService);
		kitchenOrderRef = kitchenOrderRepository.nextIdentity();
		kitchenOrder = KitchenOrder.builder()
				.ref(kitchenOrderRef)
				.onlineOrderRef(new OnlineOrderRef())
				.eventLog(eventLog)
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
				.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.LARGE).build())
				.build();
		kitchenOrderRepository.add(kitchenOrder);
		kitchenOrder.startPrep();
    }

    @After
    public void tearDown() {
        this.eventLog.purgeSubscribers();
    }

    @Test
    public void on_orderPaidEvent_add_to_queue() {
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
    public void on_kitchenOrderPrepStartedEvent_start_prep_on_all_pizzas() {
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
	public void on_pizzaPrepFinished_start_pizzaBake() {
		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);

		Pizza pizza = pizzasByKitchenOrderRef.stream()
				.findFirst()
				.get();

		pizza.finishPrep();

		pizza = pizzaRepository.findByRef(pizza.getRef());
		assertThat(pizza.isBaking()).isTrue();
	}

	@Test
	public void on_pizzaBakeStarted_start_orderBake() {
		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);

		pizzasByKitchenOrderRef.forEach(Pizza::finishPrep);

		kitchenOrder = kitchenOrderRepository.findByRef(kitchenOrderRef);
		assertThat(kitchenOrder.isBaking()).isTrue();
	}

	@Test
	public void on_pizzaBakeFinished_start_orderAssembly() {
		// Load pizzas that are prepping...
		Set<Pizza> pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);
		pizzasByKitchenOrderRef.forEach(Pizza::finishPrep);

		// Load pizzas that are baking...
		pizzasByKitchenOrderRef = kitchenService.findPizzasByKitchenOrderRef(kitchenOrderRef);
		pizzasByKitchenOrderRef.forEach(Pizza::finishBake);

		// Ensure order has started assembly...
		kitchenOrder = kitchenOrderRepository.findByRef(kitchenOrderRef);
		assertThat(kitchenOrder.hasStartedAssembly()).isTrue();
	}
}
