package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.util.*;

/**
 * @author Matt Stine
 */
public class InProcessEventLog implements EventLog {
	private final Map<Topic, Set<EventHandler>> topics = new HashMap<>();
	private final Map<Topic, List<Event>> events = new HashMap<>();

	@Override
	public void publish(Topic topic, Event event) {
		List<Event> events = this.events.computeIfAbsent(topic, k -> new ArrayList<>());
		events.add(event);
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
		return this.topics.computeIfAbsent(topic, k -> new HashSet<>()).size();
	}

	@Override
	public List<Event> eventsBy(Topic topic) {
		return this.events.computeIfAbsent(topic, k -> new ArrayList<>());
	}
}
