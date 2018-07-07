package com.mattstine.dddworkshop.pizzashop.payments;

import com.mattstine.dddworkshop.pizzashop.infrastructure.RefStringGenerator;
import lombok.Data;

/**
 * @author Matt Stine
 */
@Data
public class PaymentRef {
	private final String reference;

	public PaymentRef() {
		reference = RefStringGenerator.generateRefString();
	}
}
