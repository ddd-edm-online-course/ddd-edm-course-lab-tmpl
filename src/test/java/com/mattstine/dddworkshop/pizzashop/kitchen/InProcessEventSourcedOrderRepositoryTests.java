package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

    @Test
    public void find_by_ref_hydrates_prepping_order() {
        repository.add(order);
        order.startPrep();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

    @Test
    public void find_by_ref_hydrates_prepped_order() {
        repository.add(order);
        order.startPrep();
        order.finishPrep();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref),
                        new OrderPrepFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

    @Test
    public void find_by_ref_hydrates_baking_order() {
        repository.add(order);
        order.startPrep();
        order.finishPrep();
        order.startBake();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref),
                        new OrderPrepFinishedEvent(ref),
                        new OrderBakeStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

    @Test
    public void find_by_ref_hydrates_baked_order() {
        repository.add(order);
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref),
                        new OrderPrepFinishedEvent(ref),
                        new OrderBakeStartedEvent(ref),
                        new OrderBakeFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

    @Test
    public void find_by_ref_hydrates_assembling_order() {
        repository.add(order);
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref),
                        new OrderPrepFinishedEvent(ref),
                        new OrderBakeStartedEvent(ref),
                        new OrderBakeFinishedEvent(ref),
                        new OrderAssemblyStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }

    @Test
    public void find_by_ref_hydrates_assembled_order() {
        repository.add(order);
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        order.finishAssembly();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new OrderAddedEvent(ref, order.state()),
                        new OrderPrepStartedEvent(ref),
                        new OrderPrepFinishedEvent(ref),
                        new OrderBakeStartedEvent(ref),
                        new OrderBakeFinishedEvent(ref),
                        new OrderAssemblyStartedEvent(ref),
                        new OrderAssemblyFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(order);
    }
}
