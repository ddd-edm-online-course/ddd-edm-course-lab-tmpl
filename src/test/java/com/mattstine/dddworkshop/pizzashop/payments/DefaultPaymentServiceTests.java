package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
public class DefaultPaymentServiceTests {

	private PaymentProcessor processor;
	private PaymentRepository repository;
	private EventLog eventLog;
	private DefaultPaymentService paymentService;

	@Before
	public void setUp() {
		processor = mock(PaymentProcessor.class);
		repository = mock(PaymentRepository.class);
		eventLog = mock(EventLog.class);
		paymentService = new DefaultPaymentService(processor, repository, eventLog);
	}

	@Test
	public void creates_payment_and_requests_from_processor() {
		PaymentRef ref = new PaymentRef();
		when(repository.nextIdentity()).thenReturn(ref);

		Payment payment = Payment.builder()
				.amount(Amount.of(10, 0))
				.ref(ref)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();
		payment.request();

		assertThat(paymentService.requestPaymentFor(Amount.of(10, 0)))
				.isEqualTo(ref);
		verify(repository).add(eq(payment));
		//TODO: this test is smelly...can't remember what I thought would fix it :-(
	}

	@Test
	public void receives_successful_payment_processed_event_and_updates_status() {
		PaymentRef paymentRef = new PaymentRef();
		PaymentProcessedEvent ppEvent = new PaymentProcessedEvent(paymentRef, PaymentProcessedEvent.Status.SUCCESSFUL);

		Payment payment = Payment.builder()
				.amount(Amount.of(10, 0))
				.ref(paymentRef)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();
		payment.request();

		when(repository.findById(eq(paymentRef))).thenReturn(payment);

		paymentService.receivePaymentProcessedEvent(ppEvent);

		assertThat(payment.isSuccessful()).isTrue();
	}

	@Test
	public void received_failed_payment_processed_event_and_updates_status() {
		PaymentRef paymentRef = new PaymentRef();
		PaymentProcessedEvent ppEvent = new PaymentProcessedEvent(paymentRef, PaymentProcessedEvent.Status.FAILED);

		Payment payment = Payment.builder()
				.amount(Amount.of(10, 0))
				.ref(paymentRef)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();
		payment.request();

		when(repository.findById(eq(paymentRef))).thenReturn(payment);

		paymentService.receivePaymentProcessedEvent(ppEvent);

		assertThat(payment.isFailed()).isTrue();
	}
}
