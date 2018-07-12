package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;

/**
 * @author Matt Stine
 */
public interface PaymentService {
	PaymentRef requestPaymentFor(Amount amount);
}
