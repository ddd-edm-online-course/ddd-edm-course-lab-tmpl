package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import lombok.*;
import lombok.experimental.NonFinal;

import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
@Value
@NoArgsConstructor //TODO: Smelly...can I do reflection without this?
public class Payment implements Aggregate {
	@NonFinal
	Amount amount;
	@NonFinal
	PaymentProcessor $paymentProcessor;
	@NonFinal
	PaymentRef ref;
	@NonFinal
	EventLog $eventLog;
	@NonFinal
	State state;

	@Builder
	private Payment(@NonNull Amount amount,
					@NonNull PaymentProcessor paymentProcessor,
					@NonNull PaymentRef ref,
					@NonNull EventLog eventLog) {
		this.amount = amount;
		this.$paymentProcessor = paymentProcessor;
		this.ref = ref;
		this.$eventLog = eventLog;

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

		$paymentProcessor.request(this);
		state = State.REQUESTED;
		$eventLog.publish(new Topic("payments"), new PaymentRequestedEvent(this.ref));
	}

	public void markSuccessful() {
		if (state != State.REQUESTED) {
			throw new IllegalStateException("Payment must be REQUESTED to mark successful");
		}

		state = State.SUCCESSFUL;
		$eventLog.publish(new Topic("payments"), new PaymentSuccessfulEvent(ref));
	}

	public void markFailed() {
		if (state != State.REQUESTED) {
			throw new IllegalStateException("Payment must be REQUESTED to mark failed");
		}

		state = State.FAILED;
		$eventLog.publish(new Topic("payments"), new PaymentFailedEvent(ref));
	}

	@Override
	public Payment identity() {
		return Payment.builder()
				.amount(Amount.IDENTITY)
				.eventLog(EventLog.IDENTITY)
				.paymentProcessor(PaymentProcessor.IDENTITY)
				.ref(PaymentRef.IDENTITY)
				.build();
	}

	@Override
	public BiFunction<Payment, PaymentEvent, Payment> accumulatorFunction() {
		return new Accumulator();
	}

	public enum State {
		NEW, REQUESTED, SUCCESSFUL, FAILED
	}

	static class Accumulator implements BiFunction<Payment, PaymentEvent, Payment> {
		@Override
		public Payment apply(Payment payment, PaymentEvent paymentEvent) {
			if (paymentEvent instanceof PaymentAddedEvent) {
				PaymentAddedEvent pae = (PaymentAddedEvent) paymentEvent;
				return pae.getPayment();
			} else if (paymentEvent instanceof PaymentRequestedEvent) {
				payment.state = State.REQUESTED;
				return payment;
			} else if (paymentEvent instanceof PaymentSuccessfulEvent) {
				payment.state = State.SUCCESSFUL;
				return payment;
			} else if (paymentEvent instanceof PaymentFailedEvent) {
				payment.state = State.FAILED;
				return payment;
			}
			throw new IllegalStateException("Unknown PaymentEvent");
		}
	}

}
