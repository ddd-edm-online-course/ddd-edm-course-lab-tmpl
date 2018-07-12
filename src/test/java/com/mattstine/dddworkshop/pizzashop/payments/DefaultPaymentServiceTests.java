package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
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
	public void subscribes_to_payments_topic() {
		verify(eventLog).subscribe(eq(new Topic("payments")), isA(EventHandler.class));
	}

	@Test
	public void creates_payment() {
		PaymentRef ref = new PaymentRef();
		when(repository.nextIdentity()).thenReturn(ref);

		Payment payment = Payment.builder()
				.amount(Amount.of(10, 0))
				.ref(ref)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();

		assertThat(ref)
				.isEqualTo(paymentService.createPaymentOf(Amount.of(10, 0)));

		verify(repository).add(eq(payment));
	}

	@Test
	public void requests_from_processor() {
		PaymentRef ref = new PaymentRef();
		Payment payment = Payment.builder()
				.amount(Amount.of(10, 0))
				.ref(ref)
				.paymentProcessor(processor)
				.eventLog(eventLog)
				.build();
		when(repository.findById(ref)).thenReturn(payment);

		paymentService.requestPaymentFor(ref);

		assertThat(payment.isRequested()).isTrue();
	}

}
