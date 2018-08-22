package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrder;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderPaidEvent;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import lombok.Value;

@Value
final class KitchenService {
    EventLog eventLog;
    KitchenOrderRepository kitchenOrderRepository;
    OrderingService orderingService;

    KitchenService(EventLog eventLog, KitchenOrderRepository kitchenOrderRepository, OrderingService orderingService) {
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.eventLog = eventLog;
        this.orderingService = orderingService;

        this.eventLog.subscribe(new Topic("ordering"), (e) -> {
            if (e instanceof OnlineOrderPaidEvent) {
                @SuppressWarnings("SpellCheckingInspection")
                OnlineOrderPaidEvent oope = (OnlineOrderPaidEvent) e;
                OnlineOrderRef onlineOrderRef = oope.getRef();

                OnlineOrder onlineOrder = orderingService.findByRef(onlineOrderRef);

                KitchenOrder.KitchenOrderBuilder kitchenOrderBuilder = KitchenOrder.builder()
                        .onlineOrderRef(onlineOrderRef)
                        .eventLog(eventLog)
                        .ref(kitchenOrderRepository.nextIdentity());

                onlineOrder.getPizzas().forEach((pizza -> {
                    switch (pizza.getSize()) {
                        case MEDIUM:
                            kitchenOrderBuilder.pizza(KitchenOrder.Pizza.builder().size(KitchenOrder.Pizza.Size.MEDIUM).build());
                    }
                }));

                kitchenOrderRepository.add(kitchenOrderBuilder.build());
            }
        });
    }

    KitchenOrder findKitchenOrderByOnlineOrderRef(OnlineOrderRef onlineOrderRef) {
        return kitchenOrderRepository.findByOnlineOrderRef(onlineOrderRef);
    }
}
