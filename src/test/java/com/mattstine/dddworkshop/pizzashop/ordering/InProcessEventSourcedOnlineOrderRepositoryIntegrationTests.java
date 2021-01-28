package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
@DisplayName("The integrated in-process event-sourced integrated online order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedOnlineOrderRepositoryIntegrationTests {

    private OnlineOrderRepository repository;
    private OnlineOrder onlineOrder;
    private Pizza pizza;
    private InProcessEventLog eventLog;

    @BeforeEach
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        repository = new InProcessEventSourcedOnlineOrderRepository(eventLog,
                new Topic("ordering"));
        OnlineOrderRef ref = repository.nextIdentity();
        onlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
    }

    @AfterEach
    public void tearDown() {
        this.eventLog.purgeSubscribers();
        this.eventLog.purgeEvents();
    }

    @Test
    public void should_hydrate_an_online_order_when_found_by_its_payment_reference() {
        repository.add(onlineOrder);
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        onlineOrder.assignPaymentRef(paymentRef);

        assertThat(repository.findByPaymentRef(paymentRef)).isEqualTo(onlineOrder);
    }
}
