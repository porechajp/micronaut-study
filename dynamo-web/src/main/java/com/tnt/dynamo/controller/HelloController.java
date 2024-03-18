package com.tnt.dynamo.controller;


import com.tnt.dynamo.entities.Employee;
import com.tnt.dynamo.services.StubService;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Controller("/api/hello")
public class HelloController {


    @Value("${dynamodb-local.host}")
    private String ddHost;

    @Value("${dynamodb-local.port}")
    private String ddPort;

    @Inject
    private StubService stubService;


    private final List<Map<String, Object>> data = List.of(Map.of("id", 1, "firstName", "Manu", "lastName", "Gopal", "joining", Date.from(Instant.EPOCH)),
            Map.of("id", 2, "firstName", "Jatan", "lastName", "Porecha", "joining", Date.from(Instant.EPOCH.plusSeconds(3600 * 24 * 100))));

    @Get(produces = MediaType.TEXT_PLAIN)
    public String get() {
        return "Hello ! " + ddHost + " : " + ddPort;
    }


    @Get(value = "/employees", produces = MediaType.APPLICATION_JSON)
    public Collection<Employee> employees() {

        return serialize(data, BeanIntrospection.getIntrospection(Employee.class));
    }



    @Get(value ="/stub",produces = MediaType.TEXT_PLAIN)
    public String stubGet(){
        return stubService.sayHello("Manu");
    }
    @Get(value ="/stub/hi",produces = MediaType.TEXT_PLAIN)
    public String stubHiGet(){
        return stubService.sayHi("Manu");
    }

    private Collection<Employee> serialize(List<Map<String, Object>> data, BeanIntrospection<Employee> introspection) {

        List<Employee> result = new ArrayList<>(data.size());

        for (var d : data) {

            result.add(mapToEmp(d, introspection));
        }


        return result;

    }

    private Employee mapToEmp(Map<String, Object> d, BeanIntrospection<Employee> introspection) {

        var args = introspection.getConstructorArguments();
        Object[] argValues = new Object[args.length];
        int ind = 0;

        for (var arg : args) {

            var val = d.get(arg.getName());

            if(!arg.isAssignableFrom(val.getClass())){
                throw new RuntimeException("Type mismatch : expected " + arg.getType().getName() + " vs actual "+ val.getClass().getName());
            }

            argValues[ind++] = val;

        }

        return introspection.instantiate(argValues);


    }
}
