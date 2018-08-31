package com.mattstine.lab.setup;

import com.mattstine.lab.infrastructure.FirstSubmissionTest;
import lombok.Value;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldTest {

    @Test
    @Category(FirstSubmissionTest.class)
    public void helloLabSubmission() {
        HelloWorld helloWorld = new HelloWorld("<FIX ME>");

        assertThat(helloWorld.getMessage()).isEqualTo("It works!");
    }

    @Value
    private class HelloWorld {
        String message;
    }

}
