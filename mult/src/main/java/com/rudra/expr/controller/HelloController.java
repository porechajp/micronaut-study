package com.rudra.expr.controller;


import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;

import java.util.Optional;

@Controller("/hello")
public class HelloController {

    @Get(value = "/", produces = MediaType.TEXT_PLAIN)
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public String defaultGet(){
        return "Hello";
    }

    @Get(value="/greet", produces = MediaType.TEXT_PLAIN)
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public String greet(@QueryValue("name") String name, @QueryValue("style") Optional<Integer> style){

        if(style.orElse(0) == 1)
            return "Hello "+name;

        return "Hi "+name;
    }
    @Get(value="/shout", produces = MediaType.TEXT_PLAIN)
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public String shout(@QueryValue("name") String name){

        return "HEY "+name.toUpperCase()+"!";
    }

}
