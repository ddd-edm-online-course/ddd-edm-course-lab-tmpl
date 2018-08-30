package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class PizzaRef implements Ref {
    public static final PizzaRef IDENTITY = new PizzaRef("");
    String reference = null;

    public PizzaRef() {
        // Use RefStringGenerator here!
    }

    @SuppressWarnings("SameParameterValue")
    private PizzaRef(String reference) {
    }

    @Override
    public String getReference() {
        return reference;
    }
}
