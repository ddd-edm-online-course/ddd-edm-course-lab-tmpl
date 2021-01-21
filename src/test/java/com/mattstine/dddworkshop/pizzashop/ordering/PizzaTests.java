package com.mattstine.dddworkshop.pizzashop.ordering;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt Stine
 */
@DisplayName("A pizza")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class PizzaTests {

    @Test
    public void should_calculate_its_price() {
        Pizza pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
        assertThat(pizza.calculatePrice()).isEqualTo(Pizza.Size.MEDIUM.getPrice());
    }

}
