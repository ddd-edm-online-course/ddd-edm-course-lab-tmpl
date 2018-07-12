package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * @author Matt Stine
 */
@Value
public class Payment {
	Amount amount;
	PaymentProcessor paymentProcessor;
	PaymentRef ref;
	EventLog eventLog;
	@NonFinal
	State state;

	@Builder
	private Payment(@NonNull Amount amount,
					@NonNull PaymentProcessor paymentProcessor,
					@NonNull PaymentRef ref,
					@NonNull EventLog eventLog) {
		this.amount = amount;
		this.paymentProcessor = paymentProcessor;
		this.ref = ref;
		this.eventLog = eventLog;

		this.state = State.NEW;
	}

	public boolean isNew() {
		return state == State.NEW;
	}

	public boolean isRequested() {
		return state == State.REQUESTED;
	}

	public boolean isSuccessful() {
		return state == State.SUCCESSFUL;
	}

	public boolean isFailed() {
		return state == State.FAILED;
	}

	public void request() {
		if (state != State.NEW) {
			throw new IllegalStateException("Payment must be NEW to request payment");
		}

		paymentProcessor.request(this);
		state = State.REQUESTED;
		eventLog.publish(new Topic("payments"), new PaymentRequestedEvent());
	}

	public void markSuccessful() {
		if (state != State.REQUESTED) {
			throw new IllegalStateException("Payment must be REQUESTED to mark successful");
		}

		state = State.SUCCESSFUL;
		eventLog.publish(new Topic("payments"), new PaymentSuccessfulEvent(ref));
	}

	public void markFailed() {
		if (state != State.REQUESTED) {
			throw new IllegalStateException("Payment must be REQUESTED to mark failed");
		}

		state = State.FAILED;
		eventLog.publish(new Topic("payments"), new PaymentFailedEvent());
	}

	public enum State {
		NEW, REQUESTED, SUCCESSFUL, FAILED
	}
}
