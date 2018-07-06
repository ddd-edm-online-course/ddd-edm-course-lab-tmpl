package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.payments.Payment;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentProcessor;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
public class PaymentTests {

	private PaymentProcessor paymentProcessor;

	@Before
	public void setUp() throws Exception {
		paymentProcessor = mock(PaymentProcessor.class);
	}

	@Test
	public void should_request_payment_from_processor() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.build();
		payment.request();

		verify(paymentProcessor).request(payment);
	}

	@Test
	public void can_start_builder_with_processor() {
		Payment.withProcessor(paymentProcessor)
				.of(Amount.of(15,00))
				.build();
	}

	@Test
	public void build_requires_processor() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.of(Amount.of(15, 00)).build());
	}

	@Test
	public void build_requires_amount() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).build());
	}
}
