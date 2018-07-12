package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import lombok.*;

/**
 * @author Matt Stine
 */
@Value
public class Pizza {

	Size size;

	@Builder
	private Pizza(@NonNull Size size) {
		this.size = size;
	}

	public Amount calculatePrice() {
		return size.getPrice();
	}

	public enum Size {
		MEDIUM(Amount.of(6,0));

		private Amount price;

		Size(Amount price) {
			this.price = price;
		}

		public Amount getPrice() {
			return price;
		}
	}
}
