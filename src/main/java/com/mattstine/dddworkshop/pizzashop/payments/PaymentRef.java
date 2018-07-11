package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RefStringGenerator;
import lombok.Data;

/**
 * @author Matt Stine
 */
@Data
public class PaymentRef {
	private final String reference;

	PaymentRef() {
		reference = RefStringGenerator.generateRefString();
	}

	private PaymentRef(String reference) {
		this.reference = reference;
	}

	public static PaymentRef from(String reference) {
		return new PaymentRef(reference);
	}
}
