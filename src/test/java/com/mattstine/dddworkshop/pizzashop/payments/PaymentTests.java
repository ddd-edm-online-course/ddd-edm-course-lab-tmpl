package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
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

	private EventLog eventLog;
	private PaymentProcessor paymentProcessor;
	private Payment payment;

	@Before
	public void setUp() {
		paymentProcessor = mock(PaymentProcessor.class);
		eventLog = mock(EventLog.class);
		payment = Payment.of(Amount.of(15, 0))
				.withProcessor(paymentProcessor)
				.withEventLog(eventLog)
				.withId(new PaymentRef())
				.withOrderRef(new OrderRef())
				.build();
	}

	@Test
	public void new_payment_is_new() {
		assertThat(payment.isNew());
	}

	@Test
	public void should_request_payment_from_processor() {
		payment.request();
		assertThat(payment.isRequested()).isTrue();
		verify(paymentProcessor).request(payment);
	}

	@Test
	public void payment_request_should_fire_event() {
		payment.request();
		verify(eventLog).publish(new PaymentRequestedEvent());
	}

	@Test
	public void should_reflect_successful_payment() {
		payment.request();
		payment.markSuccessful();
		assertThat(payment.isSuccessful()).isTrue();
	}

	@Test
	public void payment_success_should_fire_event() {
		payment.request();
		verify(eventLog).publish(new PaymentRequestedEvent());
		payment.markSuccessful();
		verify(eventLog).publish(new PaymentSuccessfulEvent());
	}

	@Test
	public void should_reflect_failed_payment() {
		payment.request();
		payment.markFailed();
		assertThat(payment.isFailed()).isTrue();
	}

	@Test
	public void payment_failure_should_fire_event() {
		payment.request();
		verify(eventLog).publish(new PaymentRequestedEvent());
		payment.markFailed();
		verify(eventLog).publish(new PaymentFailedEvent());
	}

	@Test
	public void build_requires_processor() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.of(Amount.of(15, 0)).withId(new PaymentRef()).build());
	}

	@Test
	public void build_requires_amount() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).withId(new PaymentRef()).build());
	}

	@Test
	public void build_requires_id() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).of(Amount.of(15, 0)).build());
	}

	@Test
	public void build_requires_orderRef() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).of(Amount.of(15, 0)).withId(new PaymentRef()).build());
	}

	@Test
	public void build_requires_eventLog() {
		assertThatIllegalStateException().isThrownBy(() -> Payment.withProcessor(paymentProcessor).of(Amount.of(15, 0)).withId(new PaymentRef()).withOrderRef(new OrderRef()).build());

	}

	@Test
	public void can_only_request_from_new() {
		payment.request();
		payment.markSuccessful();
		assertThatIllegalStateException().isThrownBy(payment::request);
	}

	@Test
	public void can_only_mark_requested_payment_as_successful() {
		assertThatIllegalStateException().isThrownBy(payment::markSuccessful);
	}

	@Test
	public void can_only_mark_requested_payment_as_failed() {
		assertThatIllegalStateException().isThrownBy(payment::markFailed);
	}
}
