package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class DeliveryOrderRef implements Ref {
	public static final DeliveryOrderRef IDENTITY = new DeliveryOrderRef("");
	private String reference = null;

	@SuppressWarnings("WeakerAccess")
	public DeliveryOrderRef() {
		// Use RefStringGenerator here!
	}

	@SuppressWarnings("SameParameterValue")
	private DeliveryOrderRef(String reference) {
	}

	@Override
	public String getReference() {
		return null;
	}
}
