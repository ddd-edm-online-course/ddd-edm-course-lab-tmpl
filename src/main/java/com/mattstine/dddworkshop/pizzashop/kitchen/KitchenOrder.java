package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.AggregateState;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.function.BiFunction;

@Value
public final class KitchenOrder implements Aggregate {
    KitchenOrderRef ref;
    OnlineOrderRef onlineOrderRef;
    List<Pizza> pizzas;
    EventLog $eventLog;
    @NonFinal
    State state;

    @Builder
    private KitchenOrder(@NonNull KitchenOrderRef ref, @NonNull OnlineOrderRef onlineOrderRef, @Singular List<Pizza> pizzas, @NonNull EventLog eventLog) {
        this.ref = ref;
        this.onlineOrderRef = onlineOrderRef;
        this.pizzas = pizzas;
        this.$eventLog = eventLog;

        this.state = State.NEW;
    }

    /**
     * Private no-args ctor to support reflection ONLY.
     */
    @SuppressWarnings("unused")
    private KitchenOrder() {
        this.ref = null;
        this.onlineOrderRef = null;
        this.pizzas = null;
        this.$eventLog = null;
    }

    public boolean isNew() {
        return this.state == State.NEW;
    }

    void startPrep() {
        if (!isNew())
            throw new IllegalStateException();
        this.state = State.PREPPING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderPrepStartedEvent(this.ref));
    }

    boolean isPrepping() {
        return this.state == State.PREPPING;
    }

    void startBake() {
        if (!isPrepping())
            throw new IllegalStateException();
        this.state = State.BAKING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderBakeStartedEvent(this.ref));
    }

    boolean isBaking() {
        return this.state == State.BAKING;
    }

    void startAssembly() {
        if (!isBaking())
            throw new IllegalStateException();
        this.state = State.ASSEMBLING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderAssemblyStartedEvent(this.ref));
    }

    boolean hasStartedAssembly() {
        return this.state == State.ASSEMBLING;
    }

    void finishAssembly() {
        if (this.state != State.ASSEMBLING)
            throw new IllegalStateException();
        this.state = State.ASSEMBLED;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderAssemblyFinishedEvent(this.ref));
    }

    boolean hasFinishedAssembly() {
        return this.state == State.ASSEMBLED;
    }

    @Override
    public KitchenOrder identity() {
        return KitchenOrder.builder()
                .ref(KitchenOrderRef.IDENTITY)
                .onlineOrderRef(OnlineOrderRef.IDENTITY)
                .eventLog(EventLog.IDENTITY)
                .build();
    }

    @Override
    public BiFunction<KitchenOrder, KitchenOrderEvent, KitchenOrder> accumulatorFunction() {
        return new Accumulator();
    }

    @Override
    public OrderState state() {
        return new OrderState(ref, onlineOrderRef, pizzas);
    }

    enum State {
        NEW,
        PREPPING,
        BAKING,
        ASSEMBLING,
        ASSEMBLED
    }

    private static class Accumulator implements BiFunction<KitchenOrder, KitchenOrderEvent, KitchenOrder> {

        @Override
        public KitchenOrder apply(KitchenOrder kitchenOrder, KitchenOrderEvent kitchenOrderEvent) {
            if (kitchenOrderEvent instanceof KitchenOrderAddedEvent) {
                KitchenOrderAddedEvent koae = (KitchenOrderAddedEvent) kitchenOrderEvent;
                return KitchenOrder.builder()
                        .ref(koae.getRef())
                        .onlineOrderRef(koae.getState().getOnlineOrderRef())
                        .pizzas(koae.getState().getPizzas())
                        .eventLog(InProcessEventLog.instance())
                        .build();

            } else if (kitchenOrderEvent instanceof KitchenOrderPrepStartedEvent) {
                kitchenOrder.state = State.PREPPING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderBakeStartedEvent) {
                kitchenOrder.state = State.BAKING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderAssemblyStartedEvent) {
                kitchenOrder.state = State.ASSEMBLING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderAssemblyFinishedEvent) {
                kitchenOrder.state = State.ASSEMBLED;
                return kitchenOrder;
            }
            return null;
        }

    }

    /*
     * Pizza Value Object for OnlineOrder Details Only
     */
    @Value
    public static final class Pizza {
        Size size;

        @Builder
        private Pizza(@NonNull Size size) {
            this.size = size;
        }

        public enum Size {
            SMALL, MEDIUM, LARGE
        }
    }

    @Value
    static class OrderState implements AggregateState {
        KitchenOrderRef ref;
        OnlineOrderRef onlineOrderRef;
        List<Pizza> pizzas;
    }
}
