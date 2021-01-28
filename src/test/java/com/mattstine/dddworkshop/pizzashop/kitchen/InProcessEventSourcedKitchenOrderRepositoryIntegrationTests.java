package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("The integrated in-process event-sourced kitchen order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedKitchenOrderRepositoryIntegrationTests {

    private KitchenOrderRepository repository;
    private InProcessEventLog eventLog;
    private KitchenOrder kitchenOrder;
    private OnlineOrderRef onlineOrderRef;

    @BeforeEach
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        repository = new InProcessEventSourcedKitchenOrderRepository(eventLog,
                new Topic("kitchen_orders"));
        KitchenOrderRef ref = repository.nextIdentity();
        onlineOrderRef = new OnlineOrderRef();
        kitchenOrder = KitchenOrder.builder()
                .ref(ref)
                .onlineOrderRef(onlineOrderRef)
                .pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build())
                .eventLog(eventLog)
                .build();
    }

    @AfterEach
    public void tearDown() {
        this.eventLog.purgeSubscribers();
        this.eventLog.purgeEvents();
    }

    @Test
    @Tag("Lab5Tests")
    public void should_hydrate_a_kitchen_order_when_found_by_its_online_order_reference() {
        repository.add(kitchenOrder);

        assertThat(repository.findByOnlineOrderRef(onlineOrderRef)).isEqualTo(kitchenOrder);
    }

}
