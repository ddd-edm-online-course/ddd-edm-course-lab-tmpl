package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("A kitchen order")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class KitchenOrderTests {

    private KitchenOrder kitchenOrder;
    private EventLog eventLog;
    private KitchenOrderRef ref;
    private OnlineOrderRef onlineOrderRef;

    @BeforeEach
    public void setUp() {
        eventLog = mock(EventLog.class);
        ref = new KitchenOrderRef();
        onlineOrderRef = new OnlineOrderRef();
        kitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.SMALL).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .build();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_be_buildable() {
        assertThat(kitchenOrder).isNotNull();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_start_in_the_new_state() {
        assertThat(kitchenOrder.isNew()).isTrue();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_update_its_state_when_it_receives_the_start_prep_command() {
        kitchenOrder.startPrep();
        assertThat(kitchenOrder.isPrepping()).isTrue();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_only_start_prepping_if_it_is_in_the_new_state() {
        kitchenOrder.startPrep();
        assertThatIllegalStateException().isThrownBy(kitchenOrder::startPrep);
    }

    @Test
    @Tag("Lab1Tests")
    public void should_update_its_state_when_it_receives_the_start_bake_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        assertThat(kitchenOrder.isBaking()).isTrue();
    }

    @Test
    @Tag("Lab1Tests")
	public void should_only_start_baking_if_it_is_in_the_prepping_state() {
        assertThatIllegalStateException().isThrownBy(kitchenOrder::startBake);
    }

    @Test
    @Tag("Lab1Tests")
    public void should_update_its_state_when_it_receives_the_start_assembly_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();
        assertThat(kitchenOrder.hasStartedAssembly()).isTrue();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_only_start_assembling_if_it_is_in_the_baking_state() {
        assertThatIllegalStateException().isThrownBy(kitchenOrder::startAssembly);
    }

    @Test
    @Tag("Lab1Tests")
    public void should_update_its_state_when_it_receives_the_finish_assembly_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();
        kitchenOrder.finishAssembly();
        assertThat(kitchenOrder.hasFinishedAssembly()).isTrue();
    }

    @Test
    @Tag("Lab1Tests")
    public void should_only_finish_assembling_if_it_is_in_the_assembling_state() {
        assertThatIllegalStateException().isThrownBy(kitchenOrder::finishAssembly);
    }

    @Test
    @Tag("Lab2Tests")
    public void should_publish_an_event_when_it_receives_the_start_prep_command() {
        kitchenOrder.startPrep();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderPrepStartedEvent.class));
    }

    @Test
    @Tag("Lab2Tests")
    public void should_publish_an_event_when_it_receives_the_start_bake_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderBakeStartedEvent.class));
    }

    @Test
    @Tag("Lab2Tests")
    public void should_publish_an_event_when_it_receives_the_start_assembly_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderAssemblyStartedEvent.class));
    }

    @Test
    @Tag("Lab2Tests")
    public void should_publish_an_event_when_it_receives_the_finish_assembly_command() {
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();
        kitchenOrder.finishAssembly();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderAssemblyStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(KitchenOrderAssemblyFinishedEvent.class));
    }

    @Test
    @Tag("Lab4Tests")
    public void accumulator_function_should_return_an_added_kitchen_order() {
        KitchenOrderAddedEvent orderAddedEvent = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        assertThat(kitchenOrder.accumulatorFunction().apply(kitchenOrder.identity(), orderAddedEvent)).isEqualTo(kitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void accumulator_function_should_return_a_prepping_kitchen_order() {
        KitchenOrder expectedKitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.SMALL).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .build();
        expectedKitchenOrder.startPrep();

        KitchenOrderAddedEvent orderAddedEvent = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        kitchenOrder.accumulatorFunction().apply(kitchenOrder.identity(), orderAddedEvent);

        KitchenOrderPrepStartedEvent orderPrepStartedEvent = new KitchenOrderPrepStartedEvent(ref);
        assertThat(kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderPrepStartedEvent)).isEqualTo(expectedKitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void accumulator_function_should_return_a_baking_kitchen_order() {
        KitchenOrder expectedKitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.SMALL).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .build();
        expectedKitchenOrder.startPrep();
        expectedKitchenOrder.startBake();

        KitchenOrderAddedEvent orderAddedEvent = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        kitchenOrder.accumulatorFunction().apply(kitchenOrder.identity(), orderAddedEvent);

        KitchenOrderPrepStartedEvent orderPrepStartedEvent = new KitchenOrderPrepStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderPrepStartedEvent);

        KitchenOrderBakeStartedEvent orderBakeStartedEvent = new KitchenOrderBakeStartedEvent(ref);
        assertThat(kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderBakeStartedEvent)).isEqualTo(expectedKitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void accumulator_function_should_return_an_assembling_kitchen_order() {
        KitchenOrder expectedKitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.SMALL).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .build();
        expectedKitchenOrder.startPrep();
        expectedKitchenOrder.startBake();
        expectedKitchenOrder.startAssembly();

        KitchenOrderAddedEvent orderAddedEvent = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        kitchenOrder.accumulatorFunction().apply(kitchenOrder.identity(), orderAddedEvent);

        KitchenOrderPrepStartedEvent orderPrepStartedEvent = new KitchenOrderPrepStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderPrepStartedEvent);

        KitchenOrderBakeStartedEvent orderBakeStartedEvent = new KitchenOrderBakeStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderBakeStartedEvent);

        KitchenOrderAssemblyStartedEvent orderAssemblyStartedEvent = new KitchenOrderAssemblyStartedEvent(ref);
        assertThat(kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderAssemblyStartedEvent)).isEqualTo(expectedKitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void accumulator_function_should_return_an_assembled_kitchen_order() {
        KitchenOrder expectedKitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .eventLog(eventLog)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.SMALL).build())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .build();
        expectedKitchenOrder.startPrep();
        expectedKitchenOrder.startBake();
        expectedKitchenOrder.startAssembly();
        expectedKitchenOrder.finishAssembly();

        KitchenOrderAddedEvent orderAddedEvent = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        kitchenOrder.accumulatorFunction().apply(kitchenOrder.identity(), orderAddedEvent);

        KitchenOrderPrepStartedEvent orderPrepStartedEvent = new KitchenOrderPrepStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderPrepStartedEvent);

        KitchenOrderBakeStartedEvent orderBakeStartedEvent = new KitchenOrderBakeStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderBakeStartedEvent);

        KitchenOrderAssemblyStartedEvent orderAssemblyStartedEvent = new KitchenOrderAssemblyStartedEvent(ref);
        kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderAssemblyStartedEvent);

        KitchenOrderAssemblyFinishedEvent orderAssemblyFinishedEvent = new KitchenOrderAssemblyFinishedEvent(ref);
        assertThat(kitchenOrder.accumulatorFunction().apply(kitchenOrder, orderAssemblyFinishedEvent)).isEqualTo(expectedKitchenOrder);
    }
}
