package com.mattstine.dddworkshop.pizzashop.infrastructure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * @author Matt Stine
 */
@SuppressWarnings("unchecked")
public class InProcessEventSourcedRepository<K extends Ref, T extends Aggregate, S extends AggregateState, U extends AggregateEvent, V extends RepositoryAddEvent> {
	private final EventLog eventLog;
	private final Class<K> refClass;
	private final Class<T> aggregateClass;
	private final Class<S> aggregateStateClass;
	private final Class<V> addEventClass;
	private final Topic topic;

	protected InProcessEventSourcedRepository(EventLog eventLog,
											  Class<K> refClass,
											  Class<T> aggregateClass,
											  Class<S> aggregateStateClass,
											  Class<V> addEventClass,
											  Topic topic) {
		this.eventLog = eventLog;
		this.refClass = refClass;
		this.aggregateClass = aggregateClass;
		this.aggregateStateClass = aggregateStateClass;
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

	//TODO: add does not exhibit "collection-like" behavior...
	public void add(T aggregateInstance) {
		V addEvent;

		try {
			Constructor<V> constructor = addEventClass.getDeclaredConstructor(refClass, aggregateStateClass);
			constructor.setAccessible(true);
			addEvent = constructor.newInstance(aggregateInstance.getRef(), aggregateInstance.state());
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot instantiate add event of type: " + addEventClass.getName());
		}

		eventLog.publish(topic, addEvent);
	}

	public T findByRef(K ref) {
		T aggregate = eventLog.eventsBy(topic)
				.stream()
				.map(e -> (U) e)
				.filter(e -> ref.equals(e.getRef()))
				.reduce(retrieveIdentityValue(),
						retrieveAccumulatorFunction(),
						(t, t2) -> null);
		aggregate.setEventLog(eventLog);
		return aggregate;
	}

	private BiFunction<T, U, T> retrieveAccumulatorFunction() {
		BiFunction<T, U, T> accumulatorFunction;
		try {
			Constructor ctor = aggregateClass.getDeclaredConstructor();
			ctor.setAccessible(true);
			T aggregateInstance = (T) ctor.newInstance();
			Method accumulatorFunctionMethod = aggregateClass.getMethod("accumulatorFunction");
			accumulatorFunction = (BiFunction<T, U, T>) accumulatorFunctionMethod.invoke(aggregateInstance);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot execute method: " + aggregateClass.getName() + ".accumulatorFunction", e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("Cannot instantiate class: " + aggregateClass.getName(), e);
		}
		return accumulatorFunction;
	}

	private T retrieveIdentityValue() {
		T identity;
		try {
			Constructor ctor = aggregateClass.getDeclaredConstructor();
			ctor.setAccessible(true);
			T aggregateInstance = (T) ctor.newInstance();
			Method identityMethod = aggregateClass.getMethod("identity");
			identity = (T) identityMethod.invoke(aggregateInstance);
		} catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot execute method: " + aggregateClass.getName() + ".identity", e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot instantiate class: " + aggregateClass.getName(), e);
		}
		return identity;
	}
}
