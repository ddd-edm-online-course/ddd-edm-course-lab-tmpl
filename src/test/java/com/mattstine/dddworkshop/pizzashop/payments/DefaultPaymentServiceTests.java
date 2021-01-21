package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
@DisplayName("The default payment service")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class DefaultPaymentServiceTests {

    private PaymentProcessor processor;
    private PaymentRepository repository;
    private EventLog eventLog;
    private DefaultPaymentService paymentService;

    @BeforeEach
    public void setUp() {
        processor = mock(PaymentProcessor.class);
        repository = mock(PaymentRepository.class);
        eventLog = mock(EventLog.class);
        paymentService = new DefaultPaymentService(processor, repository, eventLog);
    }

    @Test
    public void should_subscribe_to_the_payment_processor_topic() {
        verify(eventLog).subscribe(eq(new Topic("payment_processor")), isA(EventHandler.class));
    }

    @Test
    public void should_create_a_payment() {
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
    public void should_request_a_payment_from_the_payment_processor() {
        PaymentRef ref = new PaymentRef();
        Payment payment = Payment.builder()
                .amount(Amount.of(10, 0))
                .ref(ref)
                .paymentProcessor(processor)
                .eventLog(eventLog)
                .build();
        when(repository.findByRef(ref)).thenReturn(payment);

        paymentService.requestPaymentFor(ref);

        assertThat(payment.isRequested()).isTrue();
    }

}
