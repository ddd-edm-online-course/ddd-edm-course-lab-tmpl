package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Value;

@Value
final class OrderPrepFinishedEvent implements OrderEvent {
    KitchenOrderRef ref;
}
