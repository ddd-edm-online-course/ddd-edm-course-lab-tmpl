package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Stine
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {
	private final Amount amount;
	private final PaymentProcessor paymentProcessor;
	private final PaymentRef id;
	private boolean requested;

	public static PaymentBuilder of(Amount amount) {
		return new PaymentBuilder(amount);
	}

	public static PaymentBuilder withId(PaymentRef ref) {
		return new PaymentBuilder(ref);
	}

	public static PaymentBuilder withProcessor(PaymentProcessor paymentProcessor) {
		return new PaymentBuilder(paymentProcessor);
	}

	public void request() {
		paymentProcessor.request(this);
		requested = true;
	}

	public boolean isRequested() {
		return requested;
	}

	public static class PaymentBuilder {
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

			return new Payment(amount, paymentProcessor, id);
		}
	}
}
