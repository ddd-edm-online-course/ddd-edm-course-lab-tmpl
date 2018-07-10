package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.payments.Payment;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentProcessor;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
public class PaymentTests {

	private PaymentProcessor paymentProcessor;

	@Before
	public void setUp() {
		paymentProcessor = mock(PaymentProcessor.class);
	}

	@Test
	public void new_payment_is_new() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.build();

		assertThat(payment.isNew());
	}

	@Test
	public void should_request_payment_from_processor() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.build();
		payment.request();

		assertThat(payment.isRequested()).isTrue();
		verify(paymentProcessor).request(payment);
	}

	@Test
	public void should_reflect_successful_payment() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.build();

		payment.request();
		payment.markSuccessful();
		assertThat(payment.isSuccessful()).isTrue();
	}

	@Test
	public void can_start_builder_with_processor() {
		Payment.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.of(Amount.of(15,00))
				.build();
	}

	@Test
	public void can_start_builder_with_id() {
		Payment.withId(new PaymentRef())
				.withProcessor(paymentProcessor)
				.of(Amount.of(10, 00))
				.build();
	}

	@Test
	public void build_requires_processor() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.of(Amount.of(15, 00)).withId(new PaymentRef()).build());
	}

	@Test
	public void build_requires_amount() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).withId(new PaymentRef()).build());
	}

	@Test
	public void build_requires_id() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).of(Amount.of(15,00)).build());
	}

	@Test
	public void can_only_request_from_new() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.build();

		payment.request();
		payment.markSuccessful();

		assertThatIllegalStateException().isThrownBy(payment::request);
	}

	@Test
	public void can_only_mark_requested_payment_as_successful() {
		Payment payment = Payment.of(Amount.of(15, 00))
				.withProcessor(paymentProcessor)
				.withId(new PaymentRef())
				.build();

		assertThatIllegalStateException().isThrownBy(payment::markSuccessful);
	}
}
