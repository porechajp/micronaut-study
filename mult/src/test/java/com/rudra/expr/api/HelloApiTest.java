package com.rudra.expr.api;


import io.micronaut.core.util.SupplierUtil;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.function.Supplier;

@MicronautTest
public class HelloApiTest {

    @Inject
    EmbeddedServer server;

    Supplier<HttpClient> client = SupplierUtil.memoizedNonEmpty(()->server.getApplicationContext().createBean(HttpClient.class,server.getURL()));

    @Test
    void testHelloGreet(){
        Assertions.assertEquals("Hi Manu",client.get().toBlocking().retrieve("/hello/greet?name=Manu"));
    }

    @Test
    void testHelloGreetWithOption(){
        Assertions.assertEquals("Hello Manu",client.get().toBlocking().retrieve("/hello/greet?name=Manu&style=1"));
    }

    @Test
    void testHelloShout(){
        Assertions.assertEquals("HEY MANU!",client.get().toBlocking().retrieve("/hello/shout?name=Manu"));
    }

}
