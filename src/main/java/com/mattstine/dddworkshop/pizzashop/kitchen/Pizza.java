package com.mattstine.dddworkshop.pizzashop.kitchen;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
final class Pizza {
    Size size;

    @Builder
    private Pizza(@NonNull Size size) {
        this.size = size;
    }

    enum Size {
        SMALL, MEDIUM, LARGE
    }
}
