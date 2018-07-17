package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;

class InProcessEventSourcedOrderRepository extends InProcessEventSourcedRepository<KitchenOrderRef, Order, Order.OrderState, OrderEvent, OrderAddedEvent> implements OrderRepository {
    InProcessEventSourcedOrderRepository(EventLog eventLog, Class<KitchenOrderRef> refClass, Class<Order> aggregateClass, Class<Order.OrderState> aggregateStateClass, Class<OrderAddedEvent> addEventClass, Topic topic) {
        super(eventLog,
                refClass,
                aggregateClass,
                aggregateStateClass,
                addEventClass,
                topic);
    }
}
