package com.mattstine.dddworkshop.pizzashop.suites;

import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderTests;
import com.mattstine.dddworkshop.pizzashop.kitchen.PizzaTests;
import com.mattstine.lab.infrastructure.Lab1Tests;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Matt Stine
 */
@RunWith(Categories.class)
@Suite.SuiteClasses({
		KitchenOrderTests.class,
		PizzaTests.class
})
@Categories.IncludeCategory({
		Lab1Tests.class
})
public class Lab1Suite {
}
