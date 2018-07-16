package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
public abstract class Aggregate<E extends AggregateEvent> {

    protected EventLog $eventLog;

    public abstract Aggregate identity();

    public abstract BiFunction<Aggregate, E, Aggregate> accumulatorFunction();

    public abstract Ref getRef();

    public abstract AggregateState state();

    void setEventLog(EventLog eventLog) {
        this.$eventLog = eventLog;
    }


}
