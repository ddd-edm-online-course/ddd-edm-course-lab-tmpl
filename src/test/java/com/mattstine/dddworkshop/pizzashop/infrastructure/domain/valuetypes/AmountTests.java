package com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Matt Stine
 */
@DisplayName("An amount")
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(separator = " ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
public class AmountTests {

    @Test
    public void should_add_without_overflow() {
        assertThat(Amount.of(6, 20)
                .plus(Amount.of(6, 20)))
                .isEqualTo(Amount.of(12, 40));
    }

    @Test
    public void should_add_with_perfect_overflow() {
        assertThat(Amount.of(6, 50)
                .plus(Amount.of(6, 50)))
                .isEqualTo(Amount.of(13, 0));
    }

    @Test
    public void should_add_with_imperfect_overflow() {
        assertThat(Amount.of(6, 50)
                .plus(Amount.of(6, 60)))
                .isEqualTo(Amount.of(13, 10));
    }

    @Test
    public void should_not_allow_negative_dollars() {
        assertThatIllegalArgumentException().isThrownBy(() -> Amount.of(-1, 0));
    }

    @Test
    public void should_not_allow_negative_cents() {
        assertThatIllegalArgumentException().isThrownBy(() -> Amount.of(0, -1));
    }

    @Test
    public void should_require_cents_to_be_less_than_100() {
        assertThatIllegalArgumentException().isThrownBy(() -> Amount.of(0, 100));
    }
}
