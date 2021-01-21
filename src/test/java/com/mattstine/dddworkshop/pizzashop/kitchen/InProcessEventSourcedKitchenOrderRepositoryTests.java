package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("The in-process event-sourced kitchen order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedKitchenOrderRepositoryTests {

    private KitchenOrderRepository repository;
    private EventLog eventLog;
    private KitchenOrderRef ref;
    private KitchenOrder kitchenOrder;

    @BeforeEach
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
                new Topic("kitchen_orders"));
        ref = repository.nextIdentity();
        kitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(new OnlineOrderRef())
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
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
    public void should_publish_an_event_when_a_kitchen_order_is_added() {
        repository.add(kitchenOrder);
        assertThat(kitchenOrder.state()).isNotNull();
        KitchenOrderAddedEvent event = new KitchenOrderAddedEvent(ref, kitchenOrder.state());
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), eq(event));
    }


    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_kitchen_order_when_found_by_its_reference() {
        repository.add(kitchenOrder);

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Collections.singletonList(new KitchenOrderAddedEvent(ref, kitchenOrder.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(kitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_prepping_kitchen_order_when_found_by_its_reference() {
        repository.add(kitchenOrder);
        kitchenOrder.startPrep();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new KitchenOrderAddedEvent(ref, kitchenOrder.state()),
                        new KitchenOrderPrepStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(kitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_a_baking_kitchen_order_when_found_by_its_reference() {
        repository.add(kitchenOrder);
        kitchenOrder.startPrep();
        kitchenOrder.startBake();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new KitchenOrderAddedEvent(ref, kitchenOrder.state()),
                        new KitchenOrderPrepStartedEvent(ref),
                        new KitchenOrderBakeStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(kitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_an_assembling_kitchen_order_when_found_by_its_reference() {
        repository.add(kitchenOrder);
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new KitchenOrderAddedEvent(ref, kitchenOrder.state()),
                        new KitchenOrderPrepStartedEvent(ref),
                        new KitchenOrderBakeStartedEvent(ref),
                        new KitchenOrderAssemblyStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(kitchenOrder);
    }

    @Test
    @Tag("Lab4Tests")
    public void should_hydrate_an_assembled_kitchen_order_when_found_by_its_reference() {
        repository.add(kitchenOrder);
        kitchenOrder.startPrep();
        kitchenOrder.startBake();
        kitchenOrder.startAssembly();
        kitchenOrder.finishAssembly();

        when(eventLog.eventsBy(new Topic("kitchen_orders")))
                .thenReturn(Arrays.asList(new KitchenOrderAddedEvent(ref, kitchenOrder.state()),
                        new KitchenOrderPrepStartedEvent(ref),
                        new KitchenOrderBakeStartedEvent(ref),
                        new KitchenOrderAssemblyStartedEvent(ref),
                        new KitchenOrderAssemblyFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(kitchenOrder);
    }
}
