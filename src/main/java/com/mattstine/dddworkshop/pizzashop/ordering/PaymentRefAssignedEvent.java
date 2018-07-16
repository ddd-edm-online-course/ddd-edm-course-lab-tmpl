package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class PaymentRefAssignedEvent implements OrderEvent {
    OrderRef ref;
    PaymentRef paymentRef;
}
