package com.mattstine.dddworkshop.pizzashop.infrastructure;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Stine
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Amount {
	private final int dollars;
	private final int cents;

	public static Amount of(int dollars, int cents) {
		if (dollars < 0) {
			throw new IllegalArgumentException("Cannot build Amount with Dollars < 0");
		}

		if (cents < 0) {
			throw new IllegalArgumentException("Cannot build Amount with Cents < 0");
		}

		if (cents > 99) {
			throw new IllegalArgumentException("Cannot build Amount with Cents > 99");
		}

		return new Amount(dollars, cents);
	}

	public Amount plus(Amount amount) {
		int centsTotal = this.cents + amount.cents;

		if (centsTotal / 100 == 1) {
			return new Amount(this.dollars + amount.dollars + 1,centsTotal % 100);
		}

		return new Amount(this.dollars + amount.dollars,
				centsTotal);
	}
}
