package com.rudra.test;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rudra.test.entities.User;

import java.io.IOException;
import java.util.Date;

@Command(name = "mnrtst", description = "...",
        mixinStandardHelpOptions = true)
public class MnrtstCommand implements Runnable {


    @Inject
    @Client("http://localhost:8080/")
    private HttpClient client;

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(MnrtstCommand.class, args);
    }

    public void run() {

        try (var cl = client.toBlocking()) {

            for(var i = 1; i <= 50000; i++){

                cl.exchange(HttpRequest.POST("/users", new User(i,"Manu "+i,"God", new Date())));

            }


            var data = cl.retrieve(HttpRequest.GET("/users"), Argument.listOf(User.class));

            data.forEach(System.out::println);

            System.out.println("Total records : "+ data.size());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }




        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
