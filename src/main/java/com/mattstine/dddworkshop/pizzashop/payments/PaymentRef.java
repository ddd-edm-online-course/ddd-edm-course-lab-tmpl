package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RefStringGenerator;
import lombok.Data;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public class PaymentRef {
	String reference;

	public PaymentRef() {
		reference = RefStringGenerator.generateRefString();
	}
}
