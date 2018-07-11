package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Event;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Stine
 */
@Data
@RequiredArgsConstructor
public class PaymentProcessedEvent implements Event {
	private final PaymentRef ref;
	private final Status status;

	public boolean isSuccessful() {
		return status == Status.SUCCESSFUL;
	}

	public boolean isFailed() {
		return status == Status.FAILED;
	}

	enum Status {
		SUCCESSFUL, FAILED
	}
}
