package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import lombok.Data;

/**
 * @author Matt Stine
 */
@Data
public class Payment {
	private final Amount amount;
	private final PaymentProcessor paymentProcessor;
	private final PaymentRef id;
	private final OrderRef orderRef;
	private PaymentState paymentState;
	private boolean requested;
	private boolean successful;

	private Payment(Amount amount, PaymentProcessor paymentProcessor, PaymentRef id, OrderRef orderRef) {
		this.amount = amount;
		this.paymentProcessor = paymentProcessor;
		this.id = id;
		this.orderRef = orderRef;

		this.paymentState = PaymentState.NEW;
	}

	public static PaymentBuilder of(Amount amount) {
		return new PaymentBuilder(amount);
	}

	public static PaymentBuilder withId(PaymentRef ref) {
		return new PaymentBuilder(ref);
	}

	public static PaymentBuilder withProcessor(PaymentProcessor paymentProcessor) {
		return new PaymentBuilder(paymentProcessor);
	}

	public static PaymentBuilder withOrderRef(OrderRef orderRef) {
		return new PaymentBuilder(orderRef);
	}

	public void request() {
		if (paymentState != PaymentState.NEW) {
			throw new IllegalStateException("Payment must be NEW to request payment");
		}

		paymentProcessor.request(this);
		paymentState = PaymentState.REQUESTED;
	}

	public boolean isRequested() {
		return paymentState == PaymentState.REQUESTED;
	}

	public boolean isSuccessful() {
		return paymentState == PaymentState.SUCCESSFUL;
	}

	public void markSuccessful() {
		if (paymentState != PaymentState.REQUESTED) {
			throw new IllegalStateException("Payment must be REQUESTED to mark successful");
		}

		paymentState = PaymentState.SUCCESSFUL;
	}

	public boolean isNew() {
		return paymentState == PaymentState.NEW;
	}

	public static class PaymentBuilder {
		private OrderRef orderRef;
		private Amount amount;
		private PaymentProcessor paymentProcessor;
		private PaymentRef id;

		PaymentBuilder(Amount amount) {
			this.amount = amount;
		}

		PaymentBuilder(PaymentProcessor paymentProcessor) {
			this.paymentProcessor = paymentProcessor;
		}

		PaymentBuilder(PaymentRef ref) {
			this.id = ref;
		}

		PaymentBuilder(OrderRef orderRef) {
			this.orderRef = orderRef;
		}

		public PaymentBuilder of(Amount amount) {
			this.amount = amount;
			return this;
		}

		public PaymentBuilder withId(PaymentRef ref) {
			this.id = ref;
			return this;
		}

		public PaymentBuilder withProcessor(PaymentProcessor paymentProcessor) {
			this.paymentProcessor = paymentProcessor;
			return this;
		}

		public PaymentBuilder withOrderRef(OrderRef orderRef) {
			this.orderRef = orderRef;
			return this;
		}

		public Payment build() {
			if (amount == null) {
				throw new IllegalStateException("Cannot build Payment without Amount");
			}

			if (paymentProcessor == null) {
				throw new IllegalStateException("Cannot build Payment without PaymentProcessor");
			}

			if (id == null) {
				throw new IllegalStateException("Cannot build Payment without PaymentRef");
			}

			if (orderRef == null) {
				throw new IllegalStateException("Cannot build Payment without OrderRef");
			}

			return new Payment(amount, paymentProcessor, id, orderRef);
		}
	}
}
