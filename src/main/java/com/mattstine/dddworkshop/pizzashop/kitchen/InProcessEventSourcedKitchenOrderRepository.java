package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;

class InProcessEventSourcedKitchenOrderRepository extends InProcessEventSourcedRepository<KitchenOrderRef, KitchenOrder, KitchenOrder.OrderState, KitchenOrderEvent, KitchenOrderAddedEvent> implements KitchenOrderRepository {
    InProcessEventSourcedKitchenOrderRepository(EventLog eventLog, Class<KitchenOrderRef> refClass, Class<KitchenOrder> aggregateClass, Class<KitchenOrder.OrderState> aggregateStateClass, Class<KitchenOrderAddedEvent> addEventClass, Topic topic) {
        super(eventLog,
                refClass,
                aggregateClass,
                aggregateStateClass,
                addEventClass,
                topic);
    }
}
