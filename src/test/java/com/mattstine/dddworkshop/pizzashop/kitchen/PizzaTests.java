package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("A pizza")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class PizzaTests {

    private Pizza pizza;
    private EventLog eventLog;
    private PizzaRef ref;
    private KitchenOrderRef kitchenOrderRef;

    @BeforeEach
    public void setUp() {
        eventLog = mock(EventLog.class);
        ref = new PizzaRef();
        kitchenOrderRef = new KitchenOrderRef();
        pizza = Pizza.builder()
                .ref(ref)
                .eventLog(eventLog)
                .kitchenOrderRef(kitchenOrderRef)
                .size(Pizza.Size.MEDIUM)
                .build();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_be_buildable() {
        assertThat(pizza).isNotNull();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_start_in_the_new_state() {
        assertThat(pizza.isNew()).isTrue();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_update_its_state_when_it_starts_prepping() {
        pizza.startPrep();
        assertThat(pizza.isPrepping()).isTrue();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_only_start_prepping_if_it_is_in_the_new_state() {
        pizza.startPrep();
        assertThatIllegalStateException().isThrownBy(pizza::startPrep);
    }

    @Test
	@Tag("Lab1Tests")
    public void should_update_its_state_when_it_finishes_prepping() {
        pizza.startPrep();
        pizza.finishPrep();
        assertThat(pizza.hasFinishedPrep()).isTrue();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_only_finish_prepping_if_it_is_currently_prepping() {
        assertThatIllegalStateException().isThrownBy(pizza::finishPrep);
    }

    @Test
	@Tag("Lab1Tests")
    public void should_update_its_state_when_it_starts_baking() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        assertThat(pizza.isBaking()).isTrue();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_only_start_baking_if_it_is_prepped() {
        assertThatIllegalStateException().isThrownBy(pizza::startBake);
    }

    @SuppressWarnings("Duplicates")
    @Test
	@Tag("Lab1Tests")
    public void should_update_its_state_when_it_finishes_baking() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        pizza.finishBake();
        assertThat(pizza.hasFinishedBaking()).isTrue();
    }

    @Test
	@Tag("Lab1Tests")
    public void should_only_finish_baking_if_it_is_currently_baking() {
        assertThatIllegalStateException().isThrownBy(pizza::finishBake);
    }

    @Test
	@Tag("Lab2Tests")
    public void should_publish_an_event_event_when_it_starts_prepping() {
        pizza.startPrep();
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepStartedEvent.class));
    }

    @Test
	@Tag("Lab2Tests")
    public void should_publish_an_event_when_it_finishes_prepping() {
        pizza.startPrep();
        pizza.finishPrep();

        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepFinishedEvent.class));
    }

    @Test
	@Tag("Lab2Tests")
    public void should_publish_an_event_when_it_starts_baking() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();

        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaBakeStartedEvent.class));
    }

    @Test
	@Tag("Lab2Tests")
    public void should_publish_an_event_when_it_finishes_baking() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        pizza.finishBake();

        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("pizzas")), isA(PizzaBakeFinishedEvent.class));
    }

    @Test
	@Tag("Lab4Tests")
    public void accumulator_function_should_return_an_added_pizza() {
        PizzaAddedEvent pizzaAddedEvent = new PizzaAddedEvent(ref, pizza.state());
        assertThat(pizza.accumulatorFunction().apply(pizza.identity(), pizzaAddedEvent)).isEqualTo(pizza);
    }

    @Test
	@Tag("Lab4Tests")
    public void accumulator_function_should_return_a_prepping_pizza() {
        Pizza expectedPizza = Pizza.builder()
                .ref(ref)
                .eventLog(eventLog)
                .kitchenOrderRef(kitchenOrderRef)
                .size(Pizza.Size.MEDIUM)
                .build();
        expectedPizza.startPrep();

        PizzaAddedEvent pizzaAddedEvent = new PizzaAddedEvent(ref, pizza.state());
        pizza.accumulatorFunction().apply(pizza.identity(), pizzaAddedEvent);

        PizzaPrepStartedEvent pizzaPrepStartedEvent = new PizzaPrepStartedEvent(ref);
        assertThat(pizza.accumulatorFunction().apply(pizza, pizzaPrepStartedEvent)).isEqualTo(expectedPizza);
    }

    @Test
	@Tag("Lab4Tests")
    public void accumulator_function_should_return_a_prepped_pizza() {
        Pizza expectedPizza = Pizza.builder()
                .ref(ref)
                .eventLog(eventLog)
                .kitchenOrderRef(kitchenOrderRef)
                .size(Pizza.Size.MEDIUM)
                .build();
        expectedPizza.startPrep();
        expectedPizza.finishPrep();

        PizzaAddedEvent pizzaAddedEvent = new PizzaAddedEvent(ref, pizza.state());
        pizza.accumulatorFunction().apply(pizza.identity(), pizzaAddedEvent);

        PizzaPrepStartedEvent pizzaPrepStartedEvent = new PizzaPrepStartedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaPrepStartedEvent);

        PizzaPrepFinishedEvent pizzaPrepFinishedEvent = new PizzaPrepFinishedEvent(ref);
        assertThat(pizza.accumulatorFunction().apply(pizza, pizzaPrepFinishedEvent)).isEqualTo(expectedPizza);
    }

    @Test
	@Tag("Lab4Tests")
    public void accumulator_function_should_return_a_baking_pizza() {
        Pizza expectedPizza = Pizza.builder()
                .ref(ref)
                .eventLog(eventLog)
                .kitchenOrderRef(kitchenOrderRef)
                .size(Pizza.Size.MEDIUM)
                .build();
        expectedPizza.startPrep();
        expectedPizza.finishPrep();
        expectedPizza.startBake();

        PizzaAddedEvent pizzaAddedEvent = new PizzaAddedEvent(ref, pizza.state());
        pizza.accumulatorFunction().apply(pizza.identity(), pizzaAddedEvent);

        PizzaPrepStartedEvent pizzaPrepStartedEvent = new PizzaPrepStartedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaPrepStartedEvent);

        PizzaPrepFinishedEvent pizzaPrepFinishedEvent = new PizzaPrepFinishedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaPrepFinishedEvent);

        PizzaBakeStartedEvent pizzaBakeStartedEvent = new PizzaBakeStartedEvent(ref);
        assertThat(pizza.accumulatorFunction().apply(pizza, pizzaBakeStartedEvent)).isEqualTo(expectedPizza);
    }

    @Test
	@Tag("Lab4Tests")
    public void accumulator_function_should_return_a_baked_pizza() {
        Pizza expectedPizza = Pizza.builder()
                .ref(ref)
                .eventLog(eventLog)
                .kitchenOrderRef(kitchenOrderRef)
                .size(Pizza.Size.MEDIUM)
                .build();
        expectedPizza.startPrep();
        expectedPizza.finishPrep();
        expectedPizza.startBake();
        expectedPizza.finishBake();

        PizzaAddedEvent pizzaAddedEvent = new PizzaAddedEvent(ref, pizza.state());
        pizza.accumulatorFunction().apply(pizza.identity(), pizzaAddedEvent);

        PizzaPrepStartedEvent pizzaPrepStartedEvent = new PizzaPrepStartedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaPrepStartedEvent);

        PizzaPrepFinishedEvent pizzaPrepFinishedEvent = new PizzaPrepFinishedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaPrepFinishedEvent);

        PizzaBakeStartedEvent pizzaBakeStartedEvent = new PizzaBakeStartedEvent(ref);
        pizza.accumulatorFunction().apply(pizza, pizzaBakeStartedEvent);

        PizzaBakeFinishedEvent pizzaBakeFinishedEvent = new PizzaBakeFinishedEvent(ref);
        assertThat(pizza.accumulatorFunction().apply(pizza, pizzaBakeFinishedEvent)).isEqualTo(expectedPizza);
    }
}

