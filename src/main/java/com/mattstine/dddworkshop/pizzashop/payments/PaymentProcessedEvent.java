package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentProcessedEvent implements Event {
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
