package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
final class OrderPaidEvent implements OrderEvent {
    OrderRef ref;
}
