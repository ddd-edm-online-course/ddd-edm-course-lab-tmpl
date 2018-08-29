package com.mattstine.dddworkshop.pizzashop.suites;

import com.mattstine.dddworkshop.pizzashop.kitchen.InProcessEventSourcedKitchenOrderRepositoryTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.InProcessEventSourcedPizzaRepositoryTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.PizzaTests;
import com.mattstine.lab.infrastructure.Lab1Tests;
import com.mattstine.lab.infrastructure.Lab2Tests;
import com.mattstine.lab.infrastructure.Lab3Tests;
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
		InProcessEventSourcedPizzaRepositoryTests.class
})
@Categories.IncludeCategory({
		Lab1Tests.class,
		Lab2Tests.class,
		Lab3Tests.class
})
public class Lab3Suite {
}
