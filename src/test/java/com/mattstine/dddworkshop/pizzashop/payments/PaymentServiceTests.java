package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
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

	@Test
	public void creates_payment_and_requests_from_processor() {
		PaymentProcessor processor = mock(PaymentProcessor.class);
		PaymentRepository repository = mock(PaymentRepository.class);
		EventLog eventLog = mock(EventLog.class);

		PaymentService paymentService = new PaymentService(processor, repository, eventLog);

		PaymentRef ref = new PaymentRef();
		when(repository.nextIdentity()).thenReturn(ref);

		Payment payment = Payment.of(Amount.of(10, 0))
				.withId(ref)
				.withProcessor(processor)
				.build();
		payment.request();

		assertThat(paymentService.requestPaymentFor(new OrderRef(), Amount.of(10,0)))
				.isEqualTo(ref);
		verify(repository).add(eq(payment));
	}
}
