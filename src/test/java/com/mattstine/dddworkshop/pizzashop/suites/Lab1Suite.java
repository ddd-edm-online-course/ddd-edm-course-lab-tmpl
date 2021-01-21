package com.mattstine.dddworkshop.pizzashop.suites;

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
		PizzaTests.class
})
@IncludeTags({
		"Lab1Tests"
})
public class Lab1Suite {
}
