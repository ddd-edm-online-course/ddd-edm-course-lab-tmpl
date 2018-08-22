package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.*;
import com.mattstine.dddworkshop.pizzashop.ordering.Pizza;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KitchenServiceIntegrationTests {

    private InProcessEventLog eventLog;
    private KitchenService kitchenService;
    private OrderingService orderingService;

    @Before
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        KitchenOrderRepository kitchenOrderRepository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
                new Topic("kitchen_orders"));
        orderingService = mock(OrderingService.class);
        kitchenService = new KitchenService(eventLog, kitchenOrderRepository, orderingService);
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
                .size(Pizza.Size.MEDIUM)
                .build());

        when(orderingService.findByRef(eq(ref))).thenReturn(onlineOrder);

        eventLog.publish(new Topic("ordering"), orderPaidEvent);

        KitchenOrder kitchenOrder = kitchenService.findKitchenOrderByOnlineOrderRef(ref);
        assertThat(kitchenOrder).isNotNull();
    }
}
