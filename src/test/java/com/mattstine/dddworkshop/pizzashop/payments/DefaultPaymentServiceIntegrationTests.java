package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Matt Stine
 */
public class DefaultPaymentServiceIntegrationTests {

	private EventLog eventLog;
	private PaymentRepository repository;
	private PaymentProcessor processor;

	@Before
	public void setUp() {
		eventLog = new InProcessEventLog();
		repository = mock(PaymentRepository.class);
		processor = mock(PaymentProcessor.class);
		new DefaultPaymentService(processor,
				repository,
				eventLog);
	}

	@Test
	public void on_successful_processing_mark_success() {
		PaymentRef ref = new PaymentRef();
		Payment payment = Payment.builder()
				.eventLog(eventLog)
				.paymentProcessor(processor)
				.amount(Amount.of(10, 0))
				.ref(ref)
				.build();
		payment.request();

		when(repository.findById(ref)).thenReturn(payment);

		eventLog.publish(new Topic("payments"), new PaymentProcessedEvent(ref, PaymentProcessedEvent.Status.SUCCESSFUL));

		assertThat(payment.isSuccessful()).isTrue();
	}

	@Test
	public void on_failed_processing_mark_success() {
		PaymentRef ref = new PaymentRef();
		Payment payment = Payment.builder()
				.eventLog(eventLog)
				.paymentProcessor(processor)
				.amount(Amount.of(10, 0))
				.ref(ref)
				.build();
		payment.request();

		when(repository.findById(ref)).thenReturn(payment);

		eventLog.publish(new Topic("payments"), new PaymentProcessedEvent(ref, PaymentProcessedEvent.Status.FAILED));

		assertThat(payment.isFailed()).isTrue();
	}
}
