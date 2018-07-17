package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.RepositoryAddEvent;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class PaymentAddedEvent implements PaymentEvent, RepositoryAddEvent {
    PaymentRef ref;
    Payment.PaymentState paymentState;
}
