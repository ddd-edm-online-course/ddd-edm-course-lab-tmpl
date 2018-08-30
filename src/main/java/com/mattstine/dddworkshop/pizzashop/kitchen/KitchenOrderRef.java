package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class KitchenOrderRef implements Ref {
    public static final KitchenOrderRef IDENTITY = new KitchenOrderRef("");
    private String reference = null;

    public  KitchenOrderRef() {
        // Use RefStringGenerator here!
    }

    @SuppressWarnings("SameParameterValue")
    private KitchenOrderRef(String reference) {
    }

    @Override
    public String getReference() {
        return reference;
    }
}
