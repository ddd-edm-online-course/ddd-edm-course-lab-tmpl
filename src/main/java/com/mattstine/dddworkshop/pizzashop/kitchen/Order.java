package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Aggregate;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.AggregateState;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.function.BiFunction;

@Value
public final class Order implements Aggregate {
    KitchenOrderRef ref;
    OrderRef orderRef;
    List<Pizza> pizzas;
    EventLog $eventLog;
    @NonFinal
    State state;

    @Builder
    private Order(@NonNull KitchenOrderRef ref, @NonNull OrderRef orderRef, @Singular List<Pizza> pizzas, @NonNull EventLog eventLog) {
        this.ref = ref;
        this.orderRef = orderRef;
        this.pizzas = pizzas;
        this.$eventLog = eventLog;

        this.state = State.NEW;
    }

    /**
     * Private no-args ctor to support reflection ONLY.
     */
    private Order() {
        this.ref = null;
        this.orderRef = null;
        this.pizzas = null;
        this.$eventLog = null;
    }

    public boolean isNew() {
        return this.state == State.NEW;
    }

    public void startPrep() {
        if (this.state != State.NEW) {
            throw new IllegalStateException("Can only startPrep on NEW Order");
        }

        this.state = State.PREPPING;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderPrepStartedEvent(ref));
    }

    public boolean isPrepping() {
        return this.state == State.PREPPING;
    }

    public void finishPrep() {
        if (this.state != State.PREPPING) {
            throw new IllegalStateException("Can only finishPrep on PREPPING Order");
        }

        this.state = State.PREPPED;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderPrepFinishedEvent(ref));
    }

    public boolean hasFinishedPrep() {
        return this.state == State.PREPPED;
    }

    public void startBake() {
        if (this.state != State.PREPPED) {
            throw new IllegalStateException("Can only startBake on PREPPED Order");
        }

        this.state = State.BAKING;
        $eventLog.publish(new Topic("kitchen_orders"), new OrderBakeStartedEvent(ref));
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

    @Override
    public Order identity() {
        return Order.builder()
                .ref(KitchenOrderRef.IDENTITY)
                .orderRef(OrderRef.IDENTITY)
                .eventLog(EventLog.IDENTITY)
                .build();
    }

    @Override
    public BiFunction<Order, OrderEvent, Order> accumulatorFunction() {
        return new Accumulator();
    }

    @Override
    public OrderState state() {
        return new OrderState(ref, orderRef, pizzas);
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

    static class Accumulator implements BiFunction<Order, OrderEvent, Order> {

        @Override
        public Order apply(Order order, OrderEvent orderEvent) {
            if (orderEvent instanceof OrderAddedEvent) {
                OrderAddedEvent oae = (OrderAddedEvent) orderEvent;
                OrderState orderState = oae.getState();
                return Order.builder()
                        .eventLog(InProcessEventLog.instance())
                        .ref(orderState.getRef())
                        .orderRef(orderState.getOrderRef())
                        .pizzas(orderState.getPizzas())
                        .build();
            } else if (orderEvent instanceof OrderPrepStartedEvent) {
                order.state = State.PREPPING;
                return order;
            } else if (orderEvent instanceof OrderPrepFinishedEvent) {
                order.state = State.PREPPED;
                return order;
            } else if (orderEvent instanceof OrderBakeStartedEvent) {
                order.state = State.BAKING;
                return order;
            }
            throw new IllegalStateException("Unknown OrderEvent");
        }
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

    @Value
    static class OrderState implements AggregateState {
        KitchenOrderRef ref;
        OrderRef orderRef;
        List<Pizza> pizzas;
    }
}
