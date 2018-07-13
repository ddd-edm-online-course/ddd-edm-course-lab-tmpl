package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
@SuppressWarnings("unchecked")
public class InProcessEventSourcedRepository<K extends Ref, T extends Aggregate, S extends AggregateEvent, U extends Event> {
	private final EventLog eventLog;
	private final Class<K> refClass;
	private final Class<T> aggregateClass;
	private final Class<U> addEventClass;
	private final Topic topic;

	protected InProcessEventSourcedRepository(EventLog eventLog,
											  Class<K> refClass,
											  Class<T> aggregateClass,
											  Class<U> addEventClass,
											  Topic topic) {
		this.eventLog = eventLog;
		this.refClass = refClass;
		this.aggregateClass = aggregateClass;
		this.addEventClass = addEventClass;
		this.topic = topic;
	}

	public K nextIdentity() {
		try {
			return refClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Cannot instantiate nextIdentity of type: " + refClass.getName());
		}
	}

	public void add(T aggregateInstance) {
		U addEvent;

		try {
			Constructor<U> constructor = addEventClass.getConstructor(refClass, aggregateClass);
			addEvent = constructor.newInstance(aggregateInstance.getRef(), aggregateInstance);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot instantiate add event of type: " + addEventClass.getName());
		}

		eventLog.publish(topic, addEvent);
	}

	public T findByRef(K ref) {
		//TODO: Smelly...so much reflection to get the accumulator and identity!

		BiFunction<T, S, T> accumulatorFunction;
		try {
			T aggregateInstance = aggregateClass.newInstance();
			Method accumulatorFunctionMethod = aggregateClass.getMethod("accumulatorFunction");
			accumulatorFunction = (BiFunction<T, S, T>) accumulatorFunctionMethod.invoke(aggregateInstance);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot execute method: " + aggregateClass.getName() + ".accumulatorFunction", e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("Cannot instantiate class: " + aggregateClass.getName(), e);
		}

		T identity;
		try {
			T aggregateInstance = aggregateClass.newInstance();
			Method identityMethod = aggregateClass.getMethod("identity");
			identity = (T) identityMethod.invoke(aggregateInstance);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot execute method: " + aggregateClass.getName() + ".identity", e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot instantiate class: " + aggregateClass.getName(), e);
		}

		return eventLog.eventsBy(topic)
				.stream()
				.map(e -> (S) e)
				.filter(e -> ref.equals(e.getRef()))
				.reduce(identity,
						accumulatorFunction,
						(t, t2) -> null);
	}
}
