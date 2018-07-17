package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public final class Pizza {
    OrderRef orderRef;
    Size size;
    @NonFinal
    State state;

    @Builder
    private Pizza(@NonNull OrderRef orderRef, @NonNull Size size) {
        this.orderRef = orderRef;
        this.size = size;

        this.state = State.NEW;
    }

    public boolean isNew() {
        return this.state == State.NEW;
    }

    public void startPrep() {
        if (this.state != State.NEW) {
            throw new IllegalStateException("only NEW Pizza can startPrep");
        }

        this.state = State.PREPPING;
    }

    public boolean isPrepping() {
        return this.state == State.PREPPING;
    }

    public void finishPrep() {
        if (this.state != State.PREPPING) {
            throw new IllegalStateException("only PREPPING Pizza can finishPrep");
        }

        this.state = State.PREPPED;
    }

    public boolean hasFinishedPrep() {
        return this.state == State.PREPPED;
    }

    public void startBake() {
        if (this.state != State.PREPPED) {
            throw new IllegalStateException("only PREPPED Pizza can startBake");
        }

        this.state = State.BAKING;
    }

    public boolean isBaking() {
        return this.state == State.BAKING;
    }

    public void finishBake() {
        if (this.state != State.BAKING) {
            throw new IllegalStateException("only BAKING pizza can finishBake");
        }

        this.state = State.BAKED;
    }

    public boolean hasFinishedBaking() {
        return this.state == State.BAKED;
    }

    enum Size {
        SMALL, MEDIUM, LARGE
    }

    enum State {
        NEW,
        PREPPING,
        PREPPED,
        BAKING,
        BAKED
    }
}
