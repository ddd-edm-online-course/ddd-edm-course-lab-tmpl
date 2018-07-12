package com.mattstine.dddworkshop.pizzashop.infrastructure;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
public class InProcessEventLogTests {
	private EventLog eventLog;

	@Before
	public void setUp() {
		this.eventLog = new InProcessEventLog();
	}

	@Test
	public void shouldAddSubscriber() {
		Topic topic = new Topic("some-topic");
		eventLog.subscribe(topic, System.out::println);

		assertThat(eventLog.getNumberOfSubscribers(topic)).isEqualTo(1);
	}

	@Test
	public void shouldInvokeSubscribersOnPublish() {
		VerifiableEventHandler handler = VerifiableEventHandler.of(e -> {
		});

		Topic topic = new Topic("some-topic");
		eventLog.subscribe(topic, handler);
		eventLog.publish(topic, new Event() {});

		assertThat(handler.isInvoked()).isTrue();
	}
}
