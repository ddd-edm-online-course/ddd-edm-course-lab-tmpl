package com.mattstine.dddworkshop.pizzashop.infrastructure;

/**
 * @author Matt Stine
 */
public interface EventHandler {
	void handleEvent(Event e);
}
