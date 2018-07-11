package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Data;

/**
 * @author Matt Stine
 */
@Data
public class PaymentSuccessfulEvent implements Event {
	private final PaymentRef paymentRef;

	public PaymentRef getPaymentRef() {
		return paymentRef;
	}
}
