package com.tnt.dynamo.controller;


import io.micronaut.context.annotation.Value;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/api/hello")
public class HelloController {


    @Value("${dynamodb-local.host}")
    private String ddHost;

    @Value("${dynamodb-local.port}")
    private String ddPort;

    @Get(produces = MediaType.TEXT_PLAIN)
    public String get(){
        return "Hello ! "+ddHost + " : "+ddPort;
    }
}
