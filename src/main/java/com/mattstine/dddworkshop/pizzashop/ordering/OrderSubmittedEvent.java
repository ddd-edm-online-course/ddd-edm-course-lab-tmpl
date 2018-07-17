package com.mattstine.dddworkshop.pizzashop.ordering;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
final class OrderSubmittedEvent implements OrderEvent {
    OrderRef ref;
}
