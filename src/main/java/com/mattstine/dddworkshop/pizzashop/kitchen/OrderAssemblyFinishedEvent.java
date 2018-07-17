package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Value;

@Value
final class OrderAssemblyFinishedEvent implements OrderEvent {
    KitchenOrderRef ref;
}
