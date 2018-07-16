package com.mattstine.dddworkshop.pizzashop.infrastructure;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @author Matt Stine
 */
public interface EventLog {

	void publish(Topic topic, Event event);

	void subscribe(Topic topic, EventHandler handler);

	int getNumberOfSubscribers(Topic topic);

	List<Event> eventsBy(Topic topic);

	EventLog IDENTITY = new EventLog() {
		@Override
		public void publish(Topic topic, Event event) {
			throw new NotImplementedException();
		}

		@Override
		public void subscribe(Topic topic, EventHandler handler) {
			throw new NotImplementedException();
		}

		@Override
		public int getNumberOfSubscribers(Topic topic) {
			return -1;
		}

		@Override
		public List<Event> eventsBy(Topic topic) {
			return null;
		}
	};
}
