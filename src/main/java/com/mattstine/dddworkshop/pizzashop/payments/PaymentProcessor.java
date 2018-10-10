package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
interface PaymentProcessor {
    PaymentProcessor IDENTITY = payment -> {
        throw new RuntimeException("Not implemented!");
    };

    @SuppressWarnings({"EmptyMethod", "unused"})
    void request(Payment payment);
}
