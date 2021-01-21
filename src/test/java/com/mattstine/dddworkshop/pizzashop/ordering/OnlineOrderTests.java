package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
@DisplayName("An online order")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class OnlineOrderTests {

    private EventLog eventLog;
    private OnlineOrder onlineOrder;
    private Pizza pizza;
    private OnlineOrderRef ref;

    @BeforeEach
    public void setUp() {
        ref = new OnlineOrderRef();
        eventLog = mock(EventLog.class);
        onlineOrder = OnlineOrder.builder()
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .ref(ref)
                .build();
        pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
    }

    @Test
    public void should_be_buildable() {
        assertThat(onlineOrder.isNew()).isTrue();
    }

    @Test
    public void should_default_to_creating_a_pickup_order() {
        assertThat(onlineOrder.isPickupOrder()).isTrue();
    }

    @Test
    public void should_be_able_to_create_delivery_order() {
        onlineOrder = OnlineOrder.builder()
                .type(OnlineOrder.Type.DELIVERY)
                .eventLog(eventLog)
                .ref(new OnlineOrderRef())
                .build();
        assertThat(onlineOrder.isDeliveryOrder()).isTrue();
    }

    @Test
    public void should_be_able_to_add_a_pizza() {
        onlineOrder.addPizza(pizza);
        assertThat(onlineOrder.getPizzas()).contains(pizza);
    }

    @Test
    public void should_publish_an_event_when_adding_a_pizza() {
        onlineOrder.addPizza(pizza);
        verify(eventLog)
                .publish(eq(new Topic("ordering")),
                        eq(new PizzaAddedEvent(ref, pizza)));
    }

    @Test
    public void should_only_allow_adding_a_pizza_to_a_new_order() {
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();
        assertThatIllegalStateException().isThrownBy(() -> onlineOrder.addPizza(pizza));
    }

    @Test
    public void should_update_its_state_when_it_receives_the_submit_order_command() {
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();
        assertThat(onlineOrder.isSubmitted()).isTrue();
    }

    @Test
    public void should_publish_an_event_when_it_receives_the_submit_order_command() {
        onlineOrder.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
        onlineOrder.submit();
        verify(eventLog)
                .publish(eq(new Topic("ordering")),
                        isA(OnlineOrderSubmittedEvent.class));
    }

    @Test
    public void should_require_at_least_one_pizza_in_its_state_to_handle_the_submit_order_command() {
        assertThatIllegalStateException()
                .isThrownBy(() -> onlineOrder.submit());
    }

    @Test
    public void should_only_handle_the_submit_order_command_from_the_new_state() {
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();
        assertThatIllegalStateException().isThrownBy(onlineOrder::submit);
    }

    @Test
    public void should_calculate_its_price() {
        onlineOrder.addPizza(pizza);
        assertThat(onlineOrder.calculatePrice()).isEqualTo(Pizza.Size.MEDIUM.getPrice());
    }

    @Test
    public void should_update_its_state_when_it_receives_the_mark_paid_command() {
        onlineOrder.addPizza(pizza);
        onlineOrder.submit();
        onlineOrder.markPaid();
        assertThat(onlineOrder.isPaid()).isTrue();
    }

    @Test
    public void should_publish_an_event_when_it_receives_the_mark_paid_command() {
        onlineOrder.addPizza(pizza);
        verify(eventLog).publish(eq(new Topic("ordering")), isA(PizzaAddedEvent.class));
        onlineOrder.submit();
        verify(eventLog).publish(eq(new Topic("ordering")), isA(OnlineOrderSubmittedEvent.class));
        onlineOrder.markPaid();
        verify(eventLog).publish(eq(new Topic("ordering")), isA(OnlineOrderPaidEvent.class));
    }

    @Test
    public void should_only_handle_the_mark_paid_command_from_the_submitted_state() {
        assertThatIllegalStateException().isThrownBy(onlineOrder::markPaid);
    }

    @Test
    public void should_publish_an_event_when_it_receives_the_assign_payment_reference_command() {
        PaymentRef paymentRef = new PaymentRef();
        onlineOrder.assignPaymentRef(paymentRef);

        verify(eventLog).publish(eq(new Topic("ordering")), isA(PaymentRefAssignedEvent.class));
    }
//accumulator_function_should_return_an_assembled_kitchen_order
    @Test
    public void accumulator_function_should_return_an_added_online_order() {
        OnlineOrderAddedEvent orderAddedEvent = new OnlineOrderAddedEvent(ref, onlineOrder.state());
        assertThat(onlineOrder.accumulatorFunction().apply(onlineOrder.identity(), orderAddedEvent)).isEqualTo(onlineOrder);
    }

    @Test
    public void accumulator_function_should_return_an_online_order_with_an_added_pizza() {
        OnlineOrder expectedOnlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOnlineOrder.addPizza(pizza);

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);

        assertThat(onlineOrder.accumulatorFunction().apply(onlineOrder, pae)).isEqualTo(expectedOnlineOrder);
    }

    @Test
    public void accumulator_function_should_return_a_submitted_online_order() {
        OnlineOrder expectedOnlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOnlineOrder.addPizza(pizza);
        expectedOnlineOrder.submit();

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        onlineOrder.accumulatorFunction().apply(onlineOrder, pae);

        OnlineOrderSubmittedEvent ose = new OnlineOrderSubmittedEvent(ref);

        assertThat(onlineOrder.accumulatorFunction().apply(onlineOrder, ose)).isEqualTo(expectedOnlineOrder);
    }

    @Test
    public void accumulator_function_should_return_an_online_order_with_an_assigned_payment_reference() {
        OnlineOrder expectedOnlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOnlineOrder.addPizza(pizza);
        expectedOnlineOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        expectedOnlineOrder.assignPaymentRef(paymentRef);

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        onlineOrder.accumulatorFunction().apply(onlineOrder, pae);

        OnlineOrderSubmittedEvent ose = new OnlineOrderSubmittedEvent(ref);
        onlineOrder.accumulatorFunction().apply(onlineOrder, ose);

        @SuppressWarnings("SpellCheckingInspection")
        PaymentRefAssignedEvent prae = new PaymentRefAssignedEvent(ref, paymentRef);
        assertThat(onlineOrder.accumulatorFunction().apply(onlineOrder, prae)).isEqualTo(expectedOnlineOrder);
    }

    @Test
    public void accumulator_function_should_return_a_paid_online_order() {
        OnlineOrder expectedOnlineOrder = OnlineOrder.builder()
                .ref(ref)
                .type(OnlineOrder.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOnlineOrder.addPizza(pizza);
        expectedOnlineOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        expectedOnlineOrder.assignPaymentRef(paymentRef);

        expectedOnlineOrder.markPaid();

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        onlineOrder.accumulatorFunction().apply(onlineOrder, pae);

        OnlineOrderSubmittedEvent ose = new OnlineOrderSubmittedEvent(ref);
        onlineOrder.accumulatorFunction().apply(onlineOrder, ose);

        @SuppressWarnings("SpellCheckingInspection")
        PaymentRefAssignedEvent prae = new PaymentRefAssignedEvent(ref, paymentRef);
        onlineOrder.accumulatorFunction().apply(onlineOrder, prae);

        OnlineOrderPaidEvent ope = new OnlineOrderPaidEvent(ref);

        assertThat(onlineOrder.accumulatorFunction().apply(onlineOrder, ope)).isEqualTo(expectedOnlineOrder);
    }

}
