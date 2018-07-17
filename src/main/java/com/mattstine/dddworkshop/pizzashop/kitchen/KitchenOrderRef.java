package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import lombok.Value;

@Value
public final class KitchenOrderRef implements Ref {
    @Override
    public String getReference() {
        return null;
    }
}
