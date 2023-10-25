package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.services.RefStringGenerator;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class PizzaRef implements Ref {
    public static final PizzaRef IDENTITY = new PizzaRef("");
    String reference;

    public PizzaRef() {
        // Use RefStringGenerator here!
        this.reference = RefStringGenerator.generateRefString();
    }

    @SuppressWarnings("SameParameterValue")
    private PizzaRef(String reference) {
        this.reference = reference;
    }

    @Override
    public String getReference() {
        return reference;
    }
}
