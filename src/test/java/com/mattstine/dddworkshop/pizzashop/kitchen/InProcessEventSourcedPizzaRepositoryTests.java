package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.lab.infrastructure.Lab3Tests;
import com.mattstine.lab.infrastructure.Lab4Tests;
import com.mattstine.lab.infrastructure.Lab5Tests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class InProcessEventSourcedPizzaRepositoryTests {

    private PizzaRepository repository;
    private EventLog eventLog;
    private PizzaRef ref;
    private Pizza pizza;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedPizzaRepository(eventLog,
                new Topic("pizzas"));
        ref = repository.nextIdentity();
        pizza = Pizza.builder()
                .ref(ref)
                .size(Pizza.Size.MEDIUM)
                .kitchenOrderRef(new KitchenOrderRef())
                .eventLog(eventLog)
                .build();
    }

    @Test
    @Category(Lab3Tests.class)
    public void provides_next_identity() {
        assertThat(ref.getReference()).isNotNull();
    }

    @Test
    @Category(Lab3Tests.class)
    public void add_fires_event() {
        repository.add(pizza);
        assertThat(pizza.state()).isNotNull();
        PizzaAddedEvent event = new PizzaAddedEvent(ref, pizza.state());
        verify(eventLog).publish(eq(new Topic("pizzas")), eq(event));
    }

    @Test
    @Category(Lab4Tests.class)
    public void find_by_ref_hydrates_added_pizza() {
        repository.add(pizza);

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Collections.singletonList(new PizzaAddedEvent(ref, pizza.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Category(Lab4Tests.class)
    public void find_by_ref_hydrates_prepping_pizza() {
        repository.add(pizza);
        pizza.startPrep();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Category(Lab4Tests.class)
    public void find_by_ref_hydrates_prepped_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Category(Lab4Tests.class)
    public void find_by_ref_hydrates_baking_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref),
                        new PizzaBakeStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Category(Lab4Tests.class)
    public void find_by_ref_hydrates_baked_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        pizza.finishBake();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref),
                        new PizzaBakeStartedEvent(ref),
                        new PizzaBakeFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Category(Lab5Tests.class)
    public void subscribes_to_pizzas_topic() {
        verify(eventLog).subscribe(eq(new Topic("pizzas")), isA(EventHandler.class));
    }
}
