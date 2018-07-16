package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
public class InProcessEventSourcedOrderRepositoryIntegrationTests {

    private OrderRepository repository;
    private Order order;
    private Pizza pizza;
    private InProcessEventLog eventLog;

    @Before
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        repository = new InProcessEventSourcedOrderRepository(eventLog,
                OrderRef.class,
                Order.class,
                Order.OrderState.class,
                OrderAddedEvent.class,
                new Topic("ordering"));
        OrderRef ref = repository.nextIdentity();
        order = Order.builder()
                .ref(ref)
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .build();
        pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
    }

    @After
    public void tearDown() {
        this.eventLog.purgeSubscribers();
    }

    @Test
    public void find_by_paymentRef_hydrates_order() {
        repository.add(order);
        order.addPizza(pizza);
        order.submit();

        PaymentRef paymentRef = new PaymentRef();
        order.assignPaymentRef(paymentRef);

        assertThat(repository.findByPaymentRef(paymentRef)).isEqualTo(order);
    }
}
