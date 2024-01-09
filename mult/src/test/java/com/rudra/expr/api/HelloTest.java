package com.rudra.expr.api;

import com.rudra.expr.controller.HelloController;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class HelloTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        if(application.isServer()){

            Assertions.assertEquals("Hello",application.getApplicationContext().getBean(HelloController.class).defaultGet());

        }
    }
}
