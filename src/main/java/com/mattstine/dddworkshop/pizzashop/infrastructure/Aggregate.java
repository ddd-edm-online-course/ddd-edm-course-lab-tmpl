package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
public interface Aggregate<E extends AggregateEvent> {

	Aggregate identity();

	BiFunction<Aggregate, E, Aggregate> accumulatorFunction();

	Ref getRef();
}
