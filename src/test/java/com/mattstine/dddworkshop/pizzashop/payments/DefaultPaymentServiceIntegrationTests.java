package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.Topic;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
        repository = new InProcessEventSourcedPaymentRepository(eventLog,
                PaymentRef.class,
                Payment.class,
                Payment.PaymentState.class,
                PaymentAddedEvent.class,
                new Topic("payments"));
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
        repository.add(payment);
        payment.request();

        eventLog.publish(new Topic("payment_processor"), new PaymentProcessedEvent(ref, PaymentProcessedEvent.Status.SUCCESSFUL));

        payment = repository.findByRef(ref);
        assertThat(payment.isSuccessful()).isTrue();
    }

    @Test
    public void on_failed_processing_mark_failed() {
        PaymentRef ref = new PaymentRef();
        Payment payment = Payment.builder()
                .eventLog(eventLog)
                .paymentProcessor(processor)
                .amount(Amount.of(10, 0))
                .ref(ref)
                .build();
        repository.add(payment);
        payment.request();

        eventLog.publish(new Topic("payment_processor"), new PaymentProcessedEvent(ref, PaymentProcessedEvent.Status.FAILED));

        payment = repository.findByRef(ref);
        assertThat(payment.isFailed()).isTrue();
    }
}
