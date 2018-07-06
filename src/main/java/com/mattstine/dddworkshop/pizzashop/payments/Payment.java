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

	public static PaymentBuilder of(Amount amount) {
		return new PaymentBuilder(amount);
	}

	public static PaymentBuilder withProcessor(PaymentProcessor paymentProcessor) {
		return new PaymentBuilder(paymentProcessor);
	}

	public void request() {
		paymentProcessor.request(this);
	}

	public static class PaymentBuilder {
		private Amount amount;
		private PaymentProcessor paymentProcessor;

		PaymentBuilder(Amount amount) {
			this.amount = amount;
		}

		PaymentBuilder(PaymentProcessor paymentProcessor) {
			this.paymentProcessor = paymentProcessor;
		}

		public PaymentBuilder of(Amount amount) {
			this.amount = amount;
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

			return new Payment(amount, paymentProcessor);
		}
	}
}
