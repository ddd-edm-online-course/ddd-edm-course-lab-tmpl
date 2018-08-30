package com.mattstine.dddworkshop.pizzashop.suites;

import com.mattstine.dddworkshop.pizzashop.kitchen.*;
import com.mattstine.lab.infrastructure.*;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Matt Stine
 */
@RunWith(Categories.class)
@Suite.SuiteClasses({
		KitchenOrderTests.class,
		PizzaTests.class,
		InProcessEventSourcedKitchenOrderRepositoryTests.class,
		InProcessEventSourcedPizzaRepositoryTests.class,
		KitchenServiceTests.class,
		KitchenServiceIntegrationTests.class
})
@Categories.IncludeCategory({
		Lab1Tests.class,
		Lab2Tests.class,
		Lab3Tests.class,
		Lab4Tests.class,
		Lab5Tests.class,
		Lab6Tests.class
})
public class Lab6Suite {
}
