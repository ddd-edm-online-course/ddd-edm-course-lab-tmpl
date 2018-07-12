package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
class PaymentFailedEvent implements Event {
}
