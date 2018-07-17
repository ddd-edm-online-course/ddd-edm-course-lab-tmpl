package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InProcessEventSourcedOrderRepositoryTests {

    private OrderRepository repository;
    private EventLog eventLog;
    private KitchenOrderRef ref;
    private Order order;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedOrderRepository(eventLog,
                KitchenOrderRef.class,
                Order.class,
                Order.OrderState.class,
                OrderAddedEvent.class,
                new Topic("kitchen_orders"));
        ref = repository.nextIdentity();
        order = Order.builder()
                .ref(ref)
                .orderRef(new OrderRef())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .eventLog(eventLog)
                .build();
    }

    @Test
    public void provides_next_identity() {
        assertThat(ref).isNotNull();
    }

    @Test
    public void add_fires_event() {
        repository.add(order);
        OrderAddedEvent event = new OrderAddedEvent(ref, order.state());
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), eq(event));
    }


    @Test
    public void find_by_ref_hydrates_added_order() {
        repository.add(order);

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Collections.singletonList(new OrderAddedEvent(ref, order.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

}
