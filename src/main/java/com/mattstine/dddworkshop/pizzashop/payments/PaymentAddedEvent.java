package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RepositoryAddEvent;
import lombok.Value;

/**
 * @author Matt Stine
 */
@SuppressWarnings("WeakerAccess")
@Value
// TODO: Smelly... class needs to be public for reflection purposes.
public class PaymentAddedEvent implements PaymentEvent, RepositoryAddEvent {
	PaymentRef ref;
	Payment.PaymentState paymentState;
}
