package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class OrderTests {

    private Order order;

    @Before
    public void setUp() {
        order = Order.builder()
                .ref(new OrderRef())
                .pizza(Pizza.builder().size(Pizza.Size.SMALL).build())
                .pizza(Pizza.builder().size(Pizza.Size.MEDIUM).build())
                .build();
    }

    @Test
    public void can_build_new_order() {
        assertThat(order).isNotNull();
    }

    @Test
    public void new_order_is_new() {
        assertThat(order.isNew()).isTrue();
    }

    @Test
    public void start_order_prep_updates_state() {
        order.startPrep();
        assertThat(order.isPrepping()).isTrue();
    }

    @Test
    public void only_new_order_can_start_prep() {
        order.startPrep();
        assertThatIllegalStateException().isThrownBy(order::startPrep);
    }

    @Test
    public void finish_order_prep_updates_state() {
        order.startPrep();
        order.finishPrep();
        assertThat(order.hasFinishedPrep()).isTrue();
    }

    @Test
    public void only_prepping_order_can_finish_prep() {
        assertThatIllegalStateException().isThrownBy(order::finishPrep);
    }

    @Test
    public void start_order_bake_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        assertThat(order.isBaking()).isTrue();
    }

    @Test
    public void only_prepped_order_can_start_bake() {
        assertThatIllegalStateException().isThrownBy(order::startBake);
    }

    @Test
    public void finish_order_bake_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        assertThat(order.hasFinishedBaking()).isTrue();
    }

    @Test
    public void only_baking_order_can_finish_bake() {
        assertThatIllegalStateException().isThrownBy(order::finishBake);
    }

    @Test
    public void start_order_assembly_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        assertThat(order.hasStartedAssembly()).isTrue();
    }

    @Test
    public void only_baked_order_can_start_assembly() {
        assertThatIllegalStateException().isThrownBy(order::startAssembly);
    }

    @Test
    public void finish_order_assembly_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        order.finishAssembly();
        assertThat(order.hasFinishedAssembly()).isTrue();
    }

    @Test
    public void only_assembling_order_can_finish_assembly() {
        assertThatIllegalStateException().isThrownBy(order::finishAssembly);
    }
}
