package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Matt Stine
 */
public class InProcessEventLog implements EventLog {
	private Map<Topic, Set<EventHandler>> topics = new HashMap<>();

	@Override
	public void publish(Topic topic, Event event) {
		Set<EventHandler> subscribers = this.topics.computeIfAbsent(topic, k -> new HashSet<>());
		subscribers
				.forEach(subscriber -> subscriber.handleEvent(event));
	}

	@Override
	public void subscribe(Topic topic, EventHandler handler) {
		Set<EventHandler> subscribers = this.topics.computeIfAbsent(topic, k -> new HashSet<>());
		subscribers.add(handler);
	}

	@Override
	public int getNumberOfSubscribers(Topic topic) {
		return topics.size();
	}
}
