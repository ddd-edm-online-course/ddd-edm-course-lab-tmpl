package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PizzaTests {

    private Pizza pizza;

    @Before
    public void setUp() {
        pizza = Pizza.builder()
                .orderRef(new OrderRef())
                .size(Pizza.Size.MEDIUM)
                .build();
    }

    @Test
    public void can_build_new_pizza() {
        assertThat(pizza).isNotNull();
    }

    @Test
    public void new_pizza_is_new() {
        assertThat(pizza.isNew()).isNotNull();
    }

    @Test
    public void start_pizza_prep_updates_state() {
        pizza.startPrep();
        assertThat(pizza.isPrepping()).isTrue();
    }

    @Test
    public void only_new_pizza_can_start_prep() {
        pizza.startPrep();
        assertThatIllegalStateException().isThrownBy(pizza::startPrep);
    }

    @Test
    public void finish_pizza_prep_updates_state() {
        pizza.startPrep();
        pizza.finishPrep();
        assertThat(pizza.hasFinishedPrep()).isTrue();
    }

    @Test
    public void only_prepping_pizza_can_finish_prep() {
        assertThatIllegalStateException().isThrownBy(pizza::finishPrep);
    }

    @Test
    public void start_pizza_bake_updates_state() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        assertThat(pizza.isBaking()).isTrue();
    }

    @Test
    public void only_prepped_pizza_can_start_bake() {
        assertThatIllegalStateException().isThrownBy(pizza::startBake);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void finish_pizza_bake_updates_state() {
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        pizza.finishBake();
        assertThat(pizza.hasFinishedBaking()).isTrue();
    }

    @Test
    public void only_baking_pizza_can_finish_bake() {
        assertThatIllegalStateException().isThrownBy(pizza::finishBake);
    }
}
