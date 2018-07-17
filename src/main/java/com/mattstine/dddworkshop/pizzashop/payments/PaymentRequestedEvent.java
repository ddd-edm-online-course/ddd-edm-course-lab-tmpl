package com.mattstine.dddworkshop.pizzashop.payments;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class PaymentRequestedEvent implements PaymentEvent {
    PaymentRef ref;
}
