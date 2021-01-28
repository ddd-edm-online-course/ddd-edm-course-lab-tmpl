package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Matt Stine
 */
@DisplayName("The integrated default payment service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultPaymentServiceIntegrationTests {

    private InProcessEventLog eventLog;
    private PaymentRepository repository;
    private PaymentProcessor processor;

    @BeforeEach
    public void setUp() {
        eventLog = InProcessEventLog.instance();
        repository = new InProcessEventSourcedPaymentRepository(eventLog,
                new Topic("payments"));
        processor = mock(PaymentProcessor.class);
        new DefaultPaymentService(processor,
                repository,
                eventLog);
    }

    @AfterEach
    public void tearDown() {
        this.eventLog.purgeSubscribers();
        this.eventLog.purgeEvents();
    }

    @Test
    public void should_mark_a_payment_as_successful_when_it_receives_the_PaymentProcessedEvent_with_successful_status() {
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
    public void should_mark_a_payment_as_failed_when_it_receives_the_PaymentProcessedEvent_with_failed_status() {
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
