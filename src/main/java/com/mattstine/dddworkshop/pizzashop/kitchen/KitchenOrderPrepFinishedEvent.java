package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Value;

@Value
final class KitchenOrderPrepFinishedEvent implements KitchenOrderEvent {
    KitchenOrderRef ref;
}
