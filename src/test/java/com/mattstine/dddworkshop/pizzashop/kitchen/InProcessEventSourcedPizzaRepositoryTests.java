package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("The in-process event-sourced pizza repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedPizzaRepositoryTests {

    private PizzaRepository repository;
    private EventLog eventLog;
    private PizzaRef ref;
    private Pizza pizza;

    @BeforeEach
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
    @Tag("Lab3Tests")
    public void should_provide_the_next_available_identity() {
        assertThat(ref.getReference()).isNotNull();
    }

    @Test
    @Tag("Lab3Tests")
    public void should_publish_an_event_when_a_pizza_is_added() {
        repository.add(pizza);
        assertThat(pizza.state()).isNotNull();
        PizzaAddedEvent event = new PizzaAddedEvent(ref, pizza.state());
        verify(eventLog).publish(eq(new Topic("pizzas")), eq(event));
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_pizza_when_found_by_its_reference() {
        repository.add(pizza);

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Collections.singletonList(new PizzaAddedEvent(ref, pizza.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_prepping_pizza_when_found_by_its_reference() {
        repository.add(pizza);
        pizza.startPrep();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_prepped_pizza_when_found_by_its_reference() {
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
    @Tag("Lab4Tests")
    public void should_hydrate_a_baking_pizza_when_found_by_its_reference() {
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
    @Tag("Lab4Tests")
    public void should_hydrate_a_baked_pizza_when_found_by_its_reference() {
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
    @Tag("Lab5Tests")
    public void should_subscribe_to_the_pizzas_topic() {
        verify(eventLog).subscribe(eq(new Topic("pizzas")), isA(EventHandler.class));
    }
}
