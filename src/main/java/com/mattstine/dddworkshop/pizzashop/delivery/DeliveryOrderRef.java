package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.services.RefStringGenerator;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class DeliveryOrderRef implements Ref {
	public static final DeliveryOrderRef IDENTITY = new DeliveryOrderRef("");
	private String reference;

	@SuppressWarnings("WeakerAccess")
	public DeliveryOrderRef() {
		// Use RefStringGenerator here!
		this.reference = RefStringGenerator.generateRefString();
	}

	@SuppressWarnings("SameParameterValue")
	private DeliveryOrderRef(String reference) {
		this.reference = reference;
	}

}
