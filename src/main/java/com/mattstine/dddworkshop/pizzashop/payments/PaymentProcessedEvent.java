package com.mattstine.dddworkshop.pizzashop.payments;

import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentProcessedEvent implements PaymentEvent {
    PaymentRef ref;
    Status status;

    public boolean isSuccessful() {
        return status == Status.SUCCESSFUL;
    }

    public boolean isFailed() {
        return status == Status.FAILED;
    }

    public enum Status {
        SUCCESSFUL, FAILED
    }
}
