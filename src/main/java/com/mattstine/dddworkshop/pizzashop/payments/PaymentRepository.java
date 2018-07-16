package com.mattstine.dddworkshop.pizzashop.payments;

/**
 * @author Matt Stine
 */
interface PaymentRepository {
    PaymentRef nextIdentity();

    void add(Payment payment);

    Payment findByRef(PaymentRef ref);
}
