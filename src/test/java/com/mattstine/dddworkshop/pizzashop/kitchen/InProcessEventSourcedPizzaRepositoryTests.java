package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InProcessEventSourcedPizzaRepositoryTests {

    private PizzaRepository repository;
    private EventLog eventLog;
    private PizzaRef ref;
    private Pizza pizza;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedPizzaRepository(eventLog,
                PizzaRef.class,
                Pizza.class,
                Pizza.PizzaState.class,
                PizzaAddedEvent.class,
                new Topic("pizzas"));
        ref = repository.nextIdentity();
        pizza = Pizza.builder()
                .ref(ref)
                .size(Pizza.Size.MEDIUM)
                .orderRef(new OrderRef())
                .eventLog(eventLog)
                .build();
    }

    @Test
    public void provides_next_identity() {
        assertThat(ref).isNotNull();
    }

    @Test
    public void add_fires_event() {
        repository.add(pizza);
        PizzaAddedEvent event = new PizzaAddedEvent(ref, pizza.state());
        verify(eventLog).publish(eq(new Topic("pizzas")), eq(event));
    }
}
