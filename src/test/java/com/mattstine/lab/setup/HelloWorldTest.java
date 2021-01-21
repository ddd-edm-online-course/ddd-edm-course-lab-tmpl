package com.mattstine.lab.setup;

import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldTest {

    @Test
    @Tag("FirstSubmissionTest")
    public void helloLabSubmission() {
        HelloWorld helloWorld = new HelloWorld("<FIX ME>");

        assertThat(helloWorld.getMessage()).isEqualTo("It works!");
    }

    @Value
    private class HelloWorld {
        String message;
    }

}
