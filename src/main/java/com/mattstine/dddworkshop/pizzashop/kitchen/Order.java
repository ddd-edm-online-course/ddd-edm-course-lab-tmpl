package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

@Value
public final class Order {
    OrderRef ref;
    List<Pizza> pizzas;
    EventLog $eventLog;
    @NonFinal
    State state;

    @Builder
    private Order(@NonNull OrderRef ref, @Singular List<Pizza> pizzas, @NonNull EventLog eventLog) {
        this.ref = ref;
        this.pizzas = pizzas;
        this.$eventLog = eventLog;

        this.state = State.NEW;
    }

    public boolean isNew() {
        return this.state == State.NEW;
    }

    public void startPrep() {
        if (this.state != State.NEW) {
            throw new IllegalStateException("Can only startPrep on NEW Order");
        }

        this.state = State.PREPPING;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderPrepStartedEvent());
    }

    public boolean isPrepping() {
        return this.state == State.PREPPING;
    }

    public void finishPrep() {
        if (this.state != State.PREPPING) {
            throw new IllegalStateException("Can only finishPrep on PREPPING Order");
        }

        this.state = State.PREPPED;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderPrepFinishedEvent());
    }

    public boolean hasFinishedPrep() {
        return this.state == State.PREPPED;
    }

    public void startBake() {
        if (this.state != State.PREPPED) {
            throw new IllegalStateException("Can only startBake on PREPPED Order");
        }

        this.state = State.BAKING;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderBakeStartedEvent());
    }

    public boolean isBaking() {
        return this.state == State.BAKING;
    }

    public void finishBake() {
        if (this.state != State.BAKING) {
            throw new IllegalStateException("Can only finishBake on BAKING Order");
        }

        this.state = State.BAKED;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderBakeFinishedEvent());
    }

    public boolean hasFinishedBaking() {
        return this.state == State.BAKED;
    }

    public void startAssembly() {
        if (this.state != State.BAKED) {
            throw new IllegalStateException("Can only startAssembly on BAKED Order");
        }

        this.state = State.ASSEMBLING;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderAssemblyStartedEvent());
    }

    public boolean hasStartedAssembly() {
        return this.state == State.ASSEMBLING;
    }

    public void finishAssembly() {
        if (this.state != State.ASSEMBLING) {
            throw new IllegalStateException("Can only finishAssembly on ASSEMBLING Order");
        }

        this.state = State.ASSEMBLED;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderAssemblyFinishedEvent());
    }

    public boolean hasFinishedAssembly() {
        return this.state == State.ASSEMBLED;
    }

    enum State {
        NEW,
        PREPPING,
        PREPPED,
        BAKING,
        BAKED,
        ASSEMBLING,
        ASSEMBLED
    }

    /*
     * Pizza Value Object for Order Details Only
     */
    @Value
    static final class Pizza {
        Size size;

        @Builder
        private Pizza(@NonNull Size size) {
            this.size = size;
        }

        enum Size {
            SMALL, MEDIUM, LARGE
        }
    }
}
