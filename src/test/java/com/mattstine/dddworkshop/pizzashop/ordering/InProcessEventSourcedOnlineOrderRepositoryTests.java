package com.mattstine.dddworkshop.pizzashop.ordering;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
@DisplayName("The in-process event-sourced online order repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedOnlineOrderRepositoryTests {

    private OnlineOrderRepository repository;
    private EventLog eventLog;
    private OnlineOrderRef ref;
    private OnlineOrder onlineOrder;
    private Pizza pizza;

    @BeforeEach
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedOnlineOrderRepository(eventLog,
                new Topic("ordering"));
        ref = repository.nextIdentity();
        onlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
    }

    @Test
    public void should_provide_the_next_available_identity() {
        assertThat(ref).isNotNull();
    }

    @Test
    public void should_publish_an_event_when_an_online_order_is_added() {
        repository.add(onlineOrder);
        OnlineOrderAddedEvent event = new OnlineOrderAddedEvent(onlineOrder.getRef(), onlineOrder.state());
        verify(eventLog).publish(eq(new Topic("ordering")), eq(event));
    }

    @Test
    public void should_hydrate_an_online_order_when_found_by_its_reference() {
        repository.add(onlineOrder);

        when(eventLog.eventsBy(new Topic("ordering")))
                .thenReturn(Collections.singletonList(new OnlineOrderAddedEvent(ref, onlineOrder.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(onlineOrder);
    }

    @Test
    public void should_hydrate_an_online_order_with_a_pizza_when_found_by_its_reference() {
        repository.add(onlineOrder);
        onlineOrder.addPizza(pizza);

        when(eventLog.eventsBy(new Topic("ordering")))
                .thenReturn(Arrays.asList(new OnlineOrderAddedEvent(ref, onlineOrder.state()),
                        new PizzaAddedEvent(ref, pizza)));

        assertThat(repository.findByRef(ref)).isEqualTo(onlineOrder);
    }

    @Test
    public void should_hydrate_a_submitted_online_order_when_found_by_its_reference() {
        repository.add(onlineOrder);
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();

        when(eventLog.eventsBy(new Topic("ordering")))
                .thenReturn(Arrays.asList(new OnlineOrderAddedEvent(ref, onlineOrder.state()),
                        new PizzaAddedEvent(ref, pizza),
                        new OnlineOrderSubmittedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(onlineOrder);
    }

    @Test
    public void should_hydrate_an_online_order_with_an_assigned_payment_reference_when_found_by_its_reference() {
        repository.add(onlineOrder);
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        onlineOrder.assignPaymentRef(paymentRef);

        when(eventLog.eventsBy(new Topic("ordering")))
                .thenReturn(Arrays.asList(new OnlineOrderAddedEvent(ref, onlineOrder.state()),
                        new PizzaAddedEvent(ref, pizza),
                        new OnlineOrderSubmittedEvent(ref),
                        new PaymentRefAssignedEvent(ref, paymentRef)));

        assertThat(repository.findByRef(ref)).isEqualTo(onlineOrder);
    }

    @Test
    public void should_hydrate_a_paid_online_order_when_found_by_its_reference() {
        repository.add(onlineOrder);
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        onlineOrder.assignPaymentRef(paymentRef);

        onlineOrder.markPaid();

        when(eventLog.eventsBy(new Topic("ordering")))
                .thenReturn(Arrays.asList(new OnlineOrderAddedEvent(ref, onlineOrder.state()),
                        new PizzaAddedEvent(ref, pizza),
                        new OnlineOrderSubmittedEvent(ref),
                        new PaymentRefAssignedEvent(ref, paymentRef),
                        new OnlineOrderPaidEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(onlineOrder);
    }

    @Test
    public void should_subscribe_to_the_ordering_topic() {
        verify(eventLog).subscribe(eq(new Topic("ordering")), isA(EventHandler.class));
    }

}
