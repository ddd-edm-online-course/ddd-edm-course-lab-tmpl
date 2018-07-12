package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentSuccessfulEvent implements Event {
	PaymentRef paymentRef;
}
