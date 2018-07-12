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
		eventLog.subscribe(new Topic("some-topic"), System.out::println);

		assertThat(eventLog.getNumberOfSubscribers(new Topic("some-topic"))).isEqualTo(1);
	}

	@Test
	public void shouldInvokeSubscribersOnPublish() {
		VerifiableEventHandler handler = VerifiableEventHandler.of(e -> {
		});

		eventLog.subscribe(new Topic("some-topic"), handler);
		eventLog.publish(new Topic("some-topic"), new Event() {});

		assertThat(handler.isInvoked()).isTrue();
	}
}
