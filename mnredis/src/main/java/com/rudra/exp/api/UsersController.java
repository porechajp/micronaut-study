package com.rudra.exp.api;


import com.rudra.exp.entities.User;
import com.rudra.exp.service.UsersService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Controller("/users")
public class UsersController {

    @Inject
    private UsersService service;


    @Post
    public void create(@Body User user){
        service.create(user);
    }

    @Put
    public HttpStatus update(@Body User user){

        if(service.update(user)){

            return HttpStatus.OK;

        }

        return HttpStatus.NOT_FOUND;
    }

    @Get
    public List<User> getAll(){
        return service.readAll();
    }

    @Get("/{id}")
    public Optional<User> getUser(@PathVariable int id){

        return service.find(id);

    }


}
