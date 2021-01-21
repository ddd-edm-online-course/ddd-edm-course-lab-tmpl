package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Matt Stine
 */
@DisplayName("A payment")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class PaymentTests {

    private EventLog eventLog;
    private PaymentProcessor paymentProcessor;
    private Payment payment;
    private PaymentRef ref;

    @BeforeEach
    public void setUp() {
        paymentProcessor = mock(PaymentProcessor.class);
        eventLog = mock(EventLog.class);
        ref = new PaymentRef();
        payment = Payment.builder()
                .amount(Amount.of(15, 0))
                .paymentProcessor(paymentProcessor)
                .eventLog(eventLog)
                .ref(ref)
                .build();
    }

    @Test
    public void should_start_in_the_new_state() {
        assertThat(payment.isNew()).isTrue();
    }

    @Test
    public void should_request_a_payment_from_the_payment_processor() {
        payment.request();
        assertThat(payment.isRequested()).isTrue();
        verify(paymentProcessor).request(payment);
    }

    @Test
    public void should_publish_an_event_when_it_requests_a_payment_from_the_payment_processor() {
        payment.request();
        verify(eventLog).publish(eq(new Topic("payments")), isA(PaymentRequestedEvent.class));
    }

    @Test
    public void should_reflect_a_successful_payment() {
        payment.request();
        payment.markSuccessful();
        assertThat(payment.isSuccessful()).isTrue();
    }

    @Test
    public void should_publish_an_event_when_a_payment_request_succeeds() {
        payment.request();
        verify(eventLog).publish(eq(new Topic("payments")), isA(PaymentRequestedEvent.class));
        payment.markSuccessful();
        verify(eventLog).publish(eq(new Topic("payments")), isA(PaymentSuccessfulEvent.class));
    }

    @Test
    public void should_reflect_a_failed_payment() {
        payment.request();
        payment.markFailed();
        assertThat(payment.isFailed()).isTrue();
    }

    @Test
    public void should_publish_an_event_when_a_payment_request_fails() {
        payment.request();
        verify(eventLog).publish(eq(new Topic("payments")), isA(PaymentRequestedEvent.class));
        payment.markFailed();
        verify(eventLog).publish(eq(new Topic("payments")), isA(PaymentFailedEvent.class));
    }

    @Test
    public void should_only_request_payment_from_the_payment_processor_if_it_is_in_the_new_state() {
        payment.request();
        payment.markSuccessful();
        assertThatIllegalStateException().isThrownBy(payment::request);
    }

    @Test
    public void should_only_mark_a_requested_payment_as_successful() {
        assertThatIllegalStateException().isThrownBy(payment::markSuccessful);
    }

    @Test
    public void should_only_mark_a_requested_payment_as_failed() {
        assertThatIllegalStateException().isThrownBy(payment::markFailed);
    }

    @Test
    public void accumulator_function_should_return_an_added_payment() {
        PaymentAddedEvent paymentAddedEvent = new PaymentAddedEvent(ref, payment.state());

        assertThat(payment.accumulatorFunction().apply(payment.identity(), paymentAddedEvent)).isEqualTo(payment);
    }

    @Test
    public void accumulator_function_should_return_a_requested_payment() {
        Payment expectedPayment = Payment.builder()
                .ref(ref)
                .eventLog(eventLog)
                .paymentProcessor(paymentProcessor)
                .amount(Amount.of(15, 0))
                .build();
        expectedPayment.request();

        PaymentRequestedEvent pre = new PaymentRequestedEvent(ref);

        assertThat(payment.accumulatorFunction().apply(payment, pre)).isEqualTo(expectedPayment);
    }

    @Test
    public void accumulator_function_should_return_a_successful_payment() {
        Payment expectedPayment = Payment.builder()
                .ref(ref)
                .eventLog(eventLog)
                .paymentProcessor(paymentProcessor)
                .amount(Amount.of(15, 0))
                .build();
        expectedPayment.request();
        expectedPayment.markSuccessful();

        PaymentSuccessfulEvent pse = new PaymentSuccessfulEvent(ref);

        assertThat(payment.accumulatorFunction().apply(payment, pse)).isEqualTo(expectedPayment);
    }

    @Test
    public void accumulator_function_should_return_a_failed_payment() {
        Payment expectedPayment = Payment.builder()
                .ref(ref)
                .eventLog(eventLog)
                .paymentProcessor(paymentProcessor)
                .amount(Amount.of(15, 0))
                .build();
        expectedPayment.request();
        expectedPayment.markFailed();

        PaymentFailedEvent pfe = new PaymentFailedEvent(ref);

        assertThat(payment.accumulatorFunction().apply(payment, pfe)).isEqualTo(expectedPayment);
    }

    @Test
    public void accumulator_function_should_throw_an_exception_with_an_unknown_event_type() {
        assertThatIllegalStateException().isThrownBy(() -> payment.accumulatorFunction().apply(payment, () -> null));
    }

}
