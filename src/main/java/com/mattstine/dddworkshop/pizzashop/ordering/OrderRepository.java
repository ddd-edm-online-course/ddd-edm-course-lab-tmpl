package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Repository;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;

/**
 * @author Matt Stine
 */
interface OrderRepository extends Repository<OrderRef, Order, Order.OrderState, OrderEvent, OrderAddedEvent> {
    void add(Order order);

    OrderRef nextIdentity();

    Order findByRef(OrderRef ref);

    Order findByPaymentRef(PaymentRef paymentRef);
}
