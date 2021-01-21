package com.mattstine.dddworkshop.pizzashop.suites;

import com.mattstine.dddworkshop.pizzashop.kitchen.InProcessEventSourcedKitchenOrderRepositoryTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.InProcessEventSourcedPizzaRepositoryTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.PizzaTests;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

/**
 * @author Matt Stine
 */
@RunWith(JUnitPlatform.class)
@SelectClasses({
		KitchenOrderTests.class,
		PizzaTests.class,
		InProcessEventSourcedKitchenOrderRepositoryTests.class,
		InProcessEventSourcedPizzaRepositoryTests.class
})
@IncludeTags({
		"Lab1Tests",
		"Lab2Tests",
		"Lab3Tests"
})
public class Lab3Suite {
}
