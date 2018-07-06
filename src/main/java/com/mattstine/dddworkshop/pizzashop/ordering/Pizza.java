package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;

/**
 * @author Matt Stine
 */
public class Pizza {
	private final PizzaSize size;

	private Pizza(PizzaSize size) {
		this.size = size;
	}

	static PizzaBuilder ofSize(PizzaSize medium) {
		return new PizzaBuilder(medium);
	}

	public PizzaSize getSize() {
		return size;
	}

	public Amount getPrice() {
		return size.getPrice();
	}

	static class PizzaBuilder {
		private final PizzaSize size;

		private PizzaBuilder(PizzaSize size) {
			this.size = size;
		}

		public Pizza build() {
			return new Pizza(size);
		}
	}
}
