package com.rudra.exp.users;


import com.rudra.exp.entities.User;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;

@MicronautTest
public class UsersApiTest {

    @Inject
    @Client("/")
    private HttpClient client;

    private static Logger logger = LoggerFactory.getLogger(UsersApiTest.class);

    //private final Supplier<HttpClient> client = SupplierUtil.memoizedNonEmpty(() -> server.getApplicationContext().createBean(HttpClient.class, server.getURL()));


    @Test
    public void testCreation() {


        logger.info("MY PROCESS ID : "+ProcessHandle.current().pid());


        try {



            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (var i = 1; i <= 1000; i++)
            createData(i);

    }

    @Test
    public void testData() {

        try (var cl = client.toBlocking()) {

            var users = cl.retrieve(HttpRequest.GET("/users"), Argument.listOf(User.class));

            Assertions.assertEquals(1000, users.size());

            //Assertions.assertEquals("Person 1", users.getFirst().firstName());



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testUpdate(){

        try (var cl = client.toBlocking()) {

            var id = 1001;

            var resp = cl.exchange(HttpRequest.POST("/users", new User(id, "Person " + id, "Nongrata " + id, new Date())));

            Assertions.assertEquals(HttpStatus.OK, resp.status());

            resp = cl.exchange(HttpRequest.PUT("/users", new User(id, "Person " + id, "ExGrata " + id, new Date())));

            Assertions.assertEquals(HttpStatus.OK, resp.status());

            var user = cl.retrieve(HttpRequest.GET("/users/101"), Argument.of(User.class));

            Assertions.assertEquals("ExGrata "+id, user.lastName());



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testUpdateNonExist(){
        try (var cl = client.toBlocking()) {

            var id = 1011;

            var resp = cl.exchange(HttpRequest.POST("/users", new User(id, "Person " + id, "Nongrata " + id, new Date())));

            Assertions.assertEquals(HttpStatus.OK, resp.status());

            var ex = Assertions.assertThrowsExactly(HttpClientResponseException.class,()->cl.exchange(HttpRequest.PUT("/users", new User(1012, "Person " + id, "ExGrata " + id, new Date()))));

            Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFetch() {
        try (var cl = client.toBlocking()) {

            var user = cl.retrieve(HttpRequest.GET("/users/42"), Argument.of(User.class));

            Assertions.assertEquals("Person 42", user.firstName());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNonExistent() {
        try (var cl = client.toBlocking()) {

            var ex =
                    Assertions.assertThrows(HttpClientResponseException.class, () -> cl.retrieve(HttpRequest.GET("/users/412"), Argument.of(User.class)));

            Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void createData(int id) {

        try (var cl = client.toBlocking()) {
            var resp = cl.exchange(HttpRequest.POST("/users", new User(id, "Person " + id, "Nongrata " + id, new Date())));

            Assertions.assertEquals(HttpStatus.OK, resp.status());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
