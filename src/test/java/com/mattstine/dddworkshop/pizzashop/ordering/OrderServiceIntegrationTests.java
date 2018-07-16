package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentService;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentSuccessfulEvent;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Matt Stine
 */
public class OrderServiceIntegrationTests {

    private EventLog eventLog;
    private OrderRepository repository;

    @Before
    public void setUp() {
        eventLog = new InProcessEventLog();
        repository = new InProcessEventSourcedOrderRepository(eventLog,
                OrderRef.class,
                Order.class,
                Order.OrderState.class,
                OrderAddedEvent.class,
                new Topic("ordering"));
        new OrderService(eventLog, repository, mock(PaymentService.class));
    }

    @Test
    public void on_successful_payment_mark_paid() {
        OrderRef orderRef = new OrderRef();
        Order order = Order.builder()
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .ref(orderRef)
                .build();
        repository.add(order);
        order.addPizza(Pizza.builder().size(Pizza.Size.MEDIUM).build());
        order.submit();
        PaymentRef paymentRef = new PaymentRef();
        order.assignPaymentRef(paymentRef);

        eventLog.publish(new Topic("payments"), new PaymentSuccessfulEvent(paymentRef));

        order = repository.findByRef(orderRef);
        assertThat(order.isPaid()).isTrue();
    }
}
