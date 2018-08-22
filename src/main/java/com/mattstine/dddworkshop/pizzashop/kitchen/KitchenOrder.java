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
    private KitchenOrder() {
        this.ref = null;
        this.onlineOrderRef = null;
        this.pizzas = null;
        this.$eventLog = null;
    }

    public boolean isNew() {
        return this.state == State.NEW;
    }

    public void startPrep() {
        if (this.state != State.NEW) {
            throw new IllegalStateException("Can only startPrep on NEW OnlineOrder");
        }

        this.state = State.PREPPING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderPrepStartedEvent(ref));
    }

    public boolean isPrepping() {
        return this.state == State.PREPPING;
    }

    public void finishPrep() {
        if (this.state != State.PREPPING) {
            throw new IllegalStateException("Can only finishPrep on PREPPING OnlineOrder");
        }

        this.state = State.PREPPED;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderPrepFinishedEvent(ref));
    }

    public boolean hasFinishedPrep() {
        return this.state == State.PREPPED;
    }

    public void startBake() {
        if (this.state != State.PREPPED) {
            throw new IllegalStateException("Can only startBake on PREPPED OnlineOrder");
        }

        this.state = State.BAKING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderBakeStartedEvent(ref));
    }

    public boolean isBaking() {
        return this.state == State.BAKING;
    }

    public void finishBake() {
        if (this.state != State.BAKING) {
            throw new IllegalStateException("Can only finishBake on BAKING OnlineOrder");
        }

        this.state = State.BAKED;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderBakeFinishedEvent(ref));
    }

    public boolean hasFinishedBaking() {
        return this.state == State.BAKED;
    }

    public void startAssembly() {
        if (this.state != State.BAKED) {
            throw new IllegalStateException("Can only startAssembly on BAKED OnlineOrder");
        }

        this.state = State.ASSEMBLING;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderAssemblyStartedEvent(ref));
    }

    public boolean hasStartedAssembly() {
        return this.state == State.ASSEMBLING;
    }

    public void finishAssembly() {
        if (this.state != State.ASSEMBLING) {
            throw new IllegalStateException("Can only finishAssembly on ASSEMBLING OnlineOrder");
        }

        this.state = State.ASSEMBLED;
        $eventLog.publish(new Topic("kitchen_orders"), new KitchenOrderAssemblyFinishedEvent(ref));
    }

    public boolean hasFinishedAssembly() {
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
        PREPPED,
        BAKING,
        BAKED,
        ASSEMBLING,
        ASSEMBLED
    }

    static class Accumulator implements BiFunction<KitchenOrder, KitchenOrderEvent, KitchenOrder> {

        @Override
        public KitchenOrder apply(KitchenOrder kitchenOrder, KitchenOrderEvent kitchenOrderEvent) {
            if (kitchenOrderEvent instanceof KitchenOrderAddedEvent) {
                KitchenOrderAddedEvent oae = (KitchenOrderAddedEvent) kitchenOrderEvent;
                OrderState orderState = oae.getState();
                return KitchenOrder.builder()
                        .eventLog(InProcessEventLog.instance())
                        .ref(orderState.getRef())
                        .onlineOrderRef(orderState.getOnlineOrderRef())
                        .pizzas(orderState.getPizzas())
                        .build();
            } else if (kitchenOrderEvent instanceof KitchenOrderPrepStartedEvent) {
                kitchenOrder.state = State.PREPPING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderPrepFinishedEvent) {
                kitchenOrder.state = State.PREPPED;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderBakeStartedEvent) {
                kitchenOrder.state = State.BAKING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderBakeFinishedEvent) {
                kitchenOrder.state = State.BAKED;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderAssemblyStartedEvent) {
                kitchenOrder.state = State.ASSEMBLING;
                return kitchenOrder;
            } else if (kitchenOrderEvent instanceof KitchenOrderAssemblyFinishedEvent) {
                kitchenOrder.state = State.ASSEMBLED;
                return kitchenOrder;
            }
            throw new IllegalStateException("Unknown KitchenOrderEvent");
        }
    }

    /*
     * Pizza Value Object for OnlineOrder Details Only
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

    @Value
    static class OrderState implements AggregateState {
        KitchenOrderRef ref;
        OnlineOrderRef onlineOrderRef;
        List<Pizza> pizzas;
    }
}
