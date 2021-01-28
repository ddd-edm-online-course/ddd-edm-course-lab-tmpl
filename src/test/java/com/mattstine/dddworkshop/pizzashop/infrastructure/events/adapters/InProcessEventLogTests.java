package com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Event;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import lombok.Value;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
@DisplayName("The in-process event log")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventLogTests {
    private InProcessEventLog eventLog;

    @BeforeEach
    public void setUp() {
        this.eventLog = InProcessEventLog.instance();
    }

    @AfterEach
    public void tearDown() {
        this.eventLog.purgeSubscribers();
        this.eventLog.purgeEvents();
    }

    @Test
    public void should_add_subscribers() {
        Topic topic = new Topic("some-topic");
        eventLog.subscribe(topic, System.out::println);

        assertThat(eventLog.getNumberOfSubscribers(topic)).isEqualTo(1);
    }


    @Test
    public void should_invoke_subscribers_on_publish() {
        VerifiableEventHandler handler = VerifiableEventHandler.of(e -> {
        });

        Topic topic = new Topic("some-topic");
        eventLog.subscribe(topic, handler);
        eventLog.publish(topic, new Event() {
        });

        assertThat(handler.isInvoked()).isTrue();
    }

    @Test
    public void should_append_to_a_topic_on_publish() {
        Topic topic = new Topic("some-topic");
        TestEvent testEvent = new TestEvent();
        eventLog.publish(topic, testEvent);
        assertThat(eventLog.eventsBy(topic)).contains(testEvent);
    }

    @Value
    private static class TestEvent implements Event {
    }
}
