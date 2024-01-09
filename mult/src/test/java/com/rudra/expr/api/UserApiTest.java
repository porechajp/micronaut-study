package com.rudra.expr.api;


import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserApiTest implements TestPropertyProvider {

    private static GenericContainer<?> redisContainer;

    //private static Map<String, String> redisProps;
    @Inject
    EmbeddedServer server;


    Supplier<HttpClient> client = SupplierUtil.memoizedNonEmpty(()->server.getApplicationContext().createBean(HttpClient.class,server.getURL()));

    Supplier<Map<String,String>> redisProps = SupplierUtil.memoizedNonEmpty(()->{

        UserApiTest.redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .waitingFor(
                        Wait.forLogMessage(".*Ready to accept connections.*\\n", 1)
                );


        redisContainer.start();
        return Map.of("redis.host", redisContainer.getContainerIpAddress().toString(), "redis.port", redisContainer.getMappedPort(6379).toString());
    });


    @BeforeAll
    public static void setupRedisContainer(){

    }

    @Test
    public void testRegister(){

        Assertions.assertEquals(HttpStatus.OK,client.get().toBlocking().exchange(HttpRequest.POST("/users/register?name=Manu","")).status());

        var data = client.get().toBlocking().retrieve(HttpRequest.GET("/users"),Argument.listOf(String.class) );

        Assertions.assertEquals(List.of("Manu"),data);

    }

    @Override
    public @NonNull Map<String, String> getProperties() {
        return redisProps.get();
    }
}
