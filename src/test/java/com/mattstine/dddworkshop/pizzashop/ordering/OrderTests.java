package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
public class OrderTests {

    private EventLog eventLog;
    private Order order;
    private Pizza pizza;
    private OrderRef ref;

    @Before
    public void setUp() {
        ref = new OrderRef();
        eventLog = mock(EventLog.class);
        order = Order.builder()
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .ref(ref)
                .build();
        pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
    }

    @Test
    public void new_order_is_new() {
        assertThat(order.isNew()).isTrue();
    }

    @Test
    public void should_create_pickup_order() {
        assertThat(order.isPickupOrder()).isTrue();
    }

    @Test
    public void should_create_delivery_order() {
        order = Order.builder()
                .type(Order.Type.DELIVERY)
                .eventLog(eventLog)
                .ref(new OrderRef())
                .build();
        assertThat(order.isDeliveryOrder()).isTrue();
    }

    @Test
    public void should_add_pizza() {
        order.addPizza(pizza);
        assertThat(order.getPizzas()).contains(pizza);
    }

    @Test
    public void adding_pizza_fires_event() {
        order.addPizza(pizza);
        verify(eventLog)
                .publish(eq(new Topic("ordering")),
                        eq(new PizzaAddedEvent(ref, pizza)));
    }

    @Test
    public void can_only_add_pizza_to_new_order() {
        order.addPizza(pizza);
        order.submit();
        assertThatIllegalStateException().isThrownBy(() -> order.addPizza(pizza));
    }

    @Test
    public void submit_order_updates_state() {
        order.addPizza(pizza);
        order.submit();
        assertThat(order.isSubmitted()).isTrue();
    }

    @Test
    public void submit_order_fires_event() {
        order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
        order.submit();
        verify(eventLog)
                .publish(eq(new Topic("ordering")),
                        isA(OrderSubmittedEvent.class));
    }

    @Test
    public void submit_requires_at_least_one_pizza() {
        assertThatIllegalStateException()
                .isThrownBy(() -> order.submit());
    }

    @Test
    public void can_only_submit_new_order() {
        order.addPizza(pizza);
        order.submit();
        assertThatIllegalStateException().isThrownBy(order::submit);
    }

    @Test
    public void calculates_price() {
        order.addPizza(pizza);
        assertThat(order.calculatePrice()).isEqualTo(Pizza.Size.MEDIUM.getPrice());
    }

    @Test
    public void mark_paid_updates_state() {
        order.addPizza(pizza);
        order.submit();
        order.markPaid();
        assertThat(order.isPaid()).isTrue();
    }

    @Test
    public void mark_paid_fires_event() {
        order.addPizza(pizza);
        verify(eventLog).publish(eq(new Topic("ordering")), isA(PizzaAddedEvent.class));
        order.submit();
        verify(eventLog).publish(eq(new Topic("ordering")), isA(OrderSubmittedEvent.class));
        order.markPaid();
        verify(eventLog).publish(eq(new Topic("ordering")), isA(OrderPaidEvent.class));
    }

    @Test
    public void can_only_mark_submitted_order_paid() {
        assertThatIllegalStateException().isThrownBy(order::markPaid);
    }

    @Test
    public void setting_payment_ref_fires_event() {
        PaymentRef paymentRef = new PaymentRef();
        order.assignPaymentRef(paymentRef);

        verify(eventLog).publish(eq(new Topic("ordering")), isA(PaymentRefAssignedEvent.class));
    }

    @Test
    public void accumulator_apply_with_orderAddedEvent_returns_order() {
        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        assertThat(order.accumulatorFunction().apply(order.identity(), orderAddedEvent)).isEqualTo(order);
    }

    @Test
    public void accumulator_apply_with_pizzaAddedEvent_updates_state() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOrder.addPizza(pizza);

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);

        assertThat(order.accumulatorFunction().apply(order, pae)).isEqualTo(expectedOrder);
    }

    @Test
    public void accumulator_apply_with_orderSubmittedEvent_updates_state() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOrder.addPizza(pizza);
        expectedOrder.submit();

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        order.accumulatorFunction().apply(order, pae);

        OrderSubmittedEvent ose = new OrderSubmittedEvent(ref);

        assertThat(order.accumulatorFunction().apply(order, ose)).isEqualTo(expectedOrder);
    }

    @Test
    public void accumulator_apply_with_paymentRefAssignedEvent_updates_state() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOrder.addPizza(pizza);
        expectedOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        expectedOrder.assignPaymentRef(paymentRef);

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        order.accumulatorFunction().apply(order, pae);

        OrderSubmittedEvent ose = new OrderSubmittedEvent(ref);
        order.accumulatorFunction().apply(order, ose);

        PaymentRefAssignedEvent prae = new PaymentRefAssignedEvent(ref, paymentRef);
        assertThat(order.accumulatorFunction().apply(order, prae)).isEqualTo(expectedOrder);
    }

    @Test
    public void accumulator_apply_with_orderPaidEvent_updates_state() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        expectedOrder.addPizza(pizza);
        expectedOrder.submit();

        PaymentRef paymentRef = new PaymentRef();
        expectedOrder.assignPaymentRef(paymentRef);

        expectedOrder.markPaid();

        PizzaAddedEvent pae = new PizzaAddedEvent(ref, pizza);
        order.accumulatorFunction().apply(order, pae);

        OrderSubmittedEvent ose = new OrderSubmittedEvent(ref);
        order.accumulatorFunction().apply(order, ose);

        PaymentRefAssignedEvent prae = new PaymentRefAssignedEvent(ref, paymentRef);
        order.accumulatorFunction().apply(order, prae);

        OrderPaidEvent ope = new OrderPaidEvent(ref);

        assertThat(order.accumulatorFunction().apply(order, ope)).isEqualTo(expectedOrder);
    }

}
