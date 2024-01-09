package com.rudra.expr.controller;


import com.rudra.expr.service.RedisPersistenceService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import jakarta.inject.Inject;

import java.util.List;

@Controller("/users")
public class UsersController {

    @Inject
    RedisPersistenceService service;

    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public List<String> defaultGet(){
        return service.readAll();
    }

    @Post(value = "/register")
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public HttpResponse register(@QueryValue("name")String name){
        service.persist(name);
        return HttpResponse.ok();
    }

}
