package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Value;

@Value
final class OrderBakeStartedEvent implements OrderEvent {
    KitchenOrderRef ref;
}
