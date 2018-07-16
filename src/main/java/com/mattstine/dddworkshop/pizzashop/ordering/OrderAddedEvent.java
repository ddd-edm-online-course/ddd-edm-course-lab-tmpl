package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RepositoryAddEvent;
import lombok.Value;

/**
 * @author Matt Stine
 */
@SuppressWarnings("WeakerAccess")
@Value
//TODO: Smelly... class needs to be public for reflection purposes
public class OrderAddedEvent implements OrderEvent, RepositoryAddEvent {
	OrderRef ref;
	Order.OrderState orderState;
}
