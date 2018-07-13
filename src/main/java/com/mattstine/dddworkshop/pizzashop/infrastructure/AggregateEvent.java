package com.mattstine.dddworkshop.pizzashop.infrastructure;

/**
 * @author Matt Stine
 */
public interface AggregateEvent extends Event {
	Ref getRef();
}
