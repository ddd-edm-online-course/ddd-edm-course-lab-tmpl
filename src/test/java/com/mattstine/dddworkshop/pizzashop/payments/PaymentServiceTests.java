package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Matt Stine
 */
public class PaymentServiceTests {

	private PaymentProcessor processor;
	private PaymentRepository repository;
	private EventLog eventLog;
	private PaymentService paymentService;

	@Before
	public void setUp() throws Exception {
		processor = mock(PaymentProcessor.class);
		repository = mock(PaymentRepository.class);
		eventLog = mock(EventLog.class);
		paymentService = new PaymentService(processor, repository, eventLog);
	}

	@Test
	public void creates_payment_and_requests_from_processor() {
		PaymentRef ref = new PaymentRef();
		when(repository.nextIdentity()).thenReturn(ref);

		Payment payment = Payment.of(Amount.of(10, 0))
				.withId(ref)
				.withProcessor(processor)
				.withOrderRef(new OrderRef())
				.build();
		payment.request();

		assertThat(paymentService.requestPaymentFor(new OrderRef(), Amount.of(10,0)))
				.isEqualTo(ref);
		verify(repository).add(eq(payment));
		//TODO: this test is smelly...
	}

	@Test
	public void receives_payment_success_event_and_updates_status() {
		PaymentRef paymentRef = new PaymentRef();
		PaymentSuccessfulEvent psEvent = new PaymentSuccessfulEvent(paymentRef);

		Payment payment = Payment.of(Amount.of(10,0))
				.withId(paymentRef)
				.withOrderRef(new OrderRef())
				.withProcessor(processor)
				.build();
		payment.request();

		when(repository.findById(eq(paymentRef))).thenReturn(payment);

		paymentService.processSuccesfulPayment(psEvent);

		assertThat(payment.isSuccessful()).isTrue();
	}
}
