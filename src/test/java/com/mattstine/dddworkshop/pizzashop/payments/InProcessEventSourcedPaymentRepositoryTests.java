package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
@DisplayName("The in-process event-sourced payment repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedPaymentRepositoryTests {

    private PaymentRepository repository;
    private EventLog eventLog;
    private PaymentRef ref;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedPaymentRepository(eventLog,
                new Topic("payments"));
        ref = repository.nextIdentity();
        payment = Payment.builder()
                .ref(ref)
                .amount(Amount.of(10, 0))
                .paymentProcessor(mock(PaymentProcessor.class))
                .eventLog(eventLog)
                .build();
    }

    @Test
    public void should_provide_the_next_available_identity() {
        assertThat(ref).isNotNull();
    }

    @Test
    public void should_publish_an_event_when_a_payment_is_added() {
        repository.add(payment);
        PaymentAddedEvent event = new PaymentAddedEvent(payment.getRef(), payment.state());
        verify(eventLog).publish(eq(new Topic("payments")), eq(event));
    }

    @Test
    public void should_hydrate_an_added_payment_when_found_by_its_reference() {
        repository.add(payment);

        when(eventLog.eventsBy(new Topic("payments")))
                .thenReturn(Collections.singletonList(new PaymentAddedEvent(ref, payment.state())));


        assertThat(repository.findByRef(ref)).isEqualTo(payment);
    }

    @Test
    public void should_hydrate_a_requested_payment_when_found_by_its_reference() {
        repository.add(payment);
        payment.request();

        when(eventLog.eventsBy(new Topic("payments")))
                .thenReturn(Arrays.asList(new PaymentAddedEvent(ref, payment.state()),
                        new PaymentRequestedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(payment);
    }

    @Test
    public void should_hydrate_a_successful_payment_when_found_by_its_reference() {
        repository.add(payment);
        payment.request();
        payment.markSuccessful();

        when(eventLog.eventsBy(new Topic("payments")))
                .thenReturn(Arrays.asList(new PaymentAddedEvent(ref, payment.state()),
                        new PaymentRequestedEvent(ref),
                        new PaymentSuccessfulEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(payment);
    }

    @Test
    public void should_hydrate_a_failed_payment_when_found_by_its_reference() {
        repository.add(payment);
        payment.request();
        payment.markFailed();

        when(eventLog.eventsBy(new Topic("payments")))
                .thenReturn(Arrays.asList(new PaymentAddedEvent(ref, payment.state()),
                        new PaymentRequestedEvent(ref),
                        new PaymentFailedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(payment);
    }
}
