package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;

/**
 * @author Matt Stine
 */
public interface PaymentService {
    PaymentRef createPaymentOf(Amount of);

    void requestPaymentFor(PaymentRef ref);
}
