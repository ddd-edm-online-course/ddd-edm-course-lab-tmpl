package com.mattstine.dddworkshop.pizzashop.infrastructure;

/**
 * @author Matt Stine
 */
public interface EventLog {
	void publish(Topic topic, Event event);

	void subscribe(Topic topic, EventHandler handler);

	int getNumberOfSubscribers(Topic topic);
}
