package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import org.junit.After;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
@DisplayName("The integrated in-process event-sourced pizza repository")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class InProcessEventSourcedPizzaRepositoryIntegrationTests {

	private PizzaRepository repository;
	private InProcessEventLog eventLog;
	private Pizza pizza;
	private KitchenOrderRef kitchenOrderRef;

	@BeforeEach
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

	@AfterEach
	public void tearDown() {
		this.eventLog.purgeSubscribers();
		this.eventLog.purgeEvents();
	}

	@Test
	@Tag("Lab5Tests")
	public void should_hydrate_a_pizza_when_found_by_its_kitchen_order_reference() {
		repository.add(pizza);

		assertThat(repository.findPizzasByKitchenOrderRef(kitchenOrderRef)).contains(pizza);
	}
}
