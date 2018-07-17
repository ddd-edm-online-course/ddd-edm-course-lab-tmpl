package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Value;

@Value
final class OrderBakeFinishedEvent implements OrderEvent {
    KitchenOrderRef ref;
}
