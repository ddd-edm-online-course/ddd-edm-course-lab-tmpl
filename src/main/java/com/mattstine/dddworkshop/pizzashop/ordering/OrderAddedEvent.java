package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.RepositoryAddEvent;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class OrderAddedEvent implements OrderEvent, RepositoryAddEvent {
    OrderRef ref;
    Order.OrderState orderState;
}
