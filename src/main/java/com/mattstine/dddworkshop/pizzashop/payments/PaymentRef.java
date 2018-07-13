package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RefStringGenerator;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentRef {
	public static final PaymentRef IDENTITY = new PaymentRef("");
	String reference;

	public PaymentRef() {
		reference = RefStringGenerator.generateRefString();
	}

	private PaymentRef(String reference) {
		this.reference = reference;
	}
}
