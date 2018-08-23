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

    @Before
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        kitchenOrderRepository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
                new Topic("kitchen_orders"));
        PizzaRepository pizzaRepository = new InProcessEventSourcedPizzaRepository(eventLog,
                new Topic("pizzas"));
        orderingService = mock(OrderingService.class);
        kitchenService = new KitchenService(eventLog, kitchenOrderRepository, pizzaRepository, orderingService);
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
        KitchenOrderRef kitchenOrderRef = kitchenOrderRepository.nextIdentity();

        KitchenOrder kitchenOrder = KitchenOrder.builder()
                .ref(kitchenOrderRef)
                .onlineOrderRef(new OnlineOrderRef())
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.LARGE).build())
                .build();

        kitchenOrderRepository.add(kitchenOrder);
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
}
