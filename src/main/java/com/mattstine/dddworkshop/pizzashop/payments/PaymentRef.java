package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Ref;
import com.mattstine.dddworkshop.pizzashop.infrastructure.RefStringGenerator;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentRef implements Ref {
	public static final PaymentRef IDENTITY = new PaymentRef("");
	String reference;

	public PaymentRef() {
		reference = RefStringGenerator.generateRefString();
	}

	@SuppressWarnings("SameParameterValue")
	private PaymentRef(String reference) {
		this.reference = reference;
	}
}
