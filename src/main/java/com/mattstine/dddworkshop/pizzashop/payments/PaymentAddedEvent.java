package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
// TODO: Smelly... class needs to be public for reflection purposes.
public class PaymentAddedEvent implements Event, PaymentEvent {
	PaymentRef ref;
	Payment payment;
}
