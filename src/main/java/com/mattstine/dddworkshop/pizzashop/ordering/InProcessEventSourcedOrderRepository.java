package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matt Stine
 */
public class InProcessEventSourcedOrderRepository extends InProcessEventSourcedRepository<OrderRef, Order, Order.OrderState, OrderEvent, OrderAddedEvent> implements OrderRepository {

    private final Map<PaymentRef, OrderRef> paymentRefToOrderRef;

    InProcessEventSourcedOrderRepository(EventLog eventLog,
                                         Class<OrderRef> refClass,
                                         Class<Order> aggregateClass,
                                         Class<Order.OrderState> aggregateStateClass,
                                         Class<OrderAddedEvent> addEventClass,
                                         Topic topic) {
        super(eventLog, refClass, aggregateClass, aggregateStateClass, addEventClass, topic);

        paymentRefToOrderRef = new HashMap<>();

        eventLog.subscribe(topic, (e) -> {
            if (e instanceof PaymentRefAssignedEvent) {
                PaymentRefAssignedEvent prae = (PaymentRefAssignedEvent) e;
                this.paymentRefToOrderRef.put(prae.getPaymentRef(), prae.getRef());
            }
        });
    }

    @Override
    public Order findByPaymentRef(PaymentRef paymentRef) {
        OrderRef ref = paymentRefToOrderRef.get(paymentRef);
        if (ref != null) {
            return this.findByRef(ref);
        }
        return null;
    }
}
