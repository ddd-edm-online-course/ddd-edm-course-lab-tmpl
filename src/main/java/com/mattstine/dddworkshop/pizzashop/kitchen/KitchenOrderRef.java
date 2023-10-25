package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.services.RefStringGenerator;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class KitchenOrderRef implements Ref {
    public static final KitchenOrderRef IDENTITY = new KitchenOrderRef("");
    private String reference;

    public  KitchenOrderRef() {
        // Use RefStringGenerator here!
        this.reference = RefStringGenerator.generateRefString();
    }

    @SuppressWarnings("SameParameterValue")
    private KitchenOrderRef(String reference) {
        this.reference = reference;
    }

    @Override
    public String getReference() {
        return reference;
    }
}
