package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.lab.infrastructure.Lab5Tests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
public class InProcessEventSourcedPizzaRepositoryIntegrationTests {

	private PizzaRepository repository;
	private InProcessEventLog eventLog;
	private Pizza pizza;
	private KitchenOrderRef kitchenOrderRef;

	@Before
	public void setUp() {
		eventLog = InProcessEventLog.instance();
		repository = new InProcessEventSourcedPizzaRepository(eventLog,
				new Topic("pizzas"));
		PizzaRef ref = repository.nextIdentity();
		kitchenOrderRef = new KitchenOrderRef();
		pizza = Pizza.builder()
				.ref(ref)
				.kitchenOrderRef(kitchenOrderRef)
				.eventLog(eventLog)
				.size(Pizza.Size.MEDIUM)
				.build();
	}

	@After
	public void tearDown() {
		this.eventLog.purgeSubscribers();
	}

	@Test
	@Category(Lab5Tests.class)
	public void find_by_kitchenOrderRef_hydrates_pizza() {
		repository.add(pizza);

		assertThat(repository.findPizzasByKitchenOrderRef(kitchenOrderRef)).contains(pizza);
	}
}
