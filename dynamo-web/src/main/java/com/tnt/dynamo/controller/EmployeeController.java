package com.tnt.dynamo.controller;


import com.tnt.dynamo.entities.Employee;
import com.tnt.dynamo.repositories.EmployeeRepository;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

@Controller("/api/employee")
public class EmployeeController {


    @Inject
    private EmployeeRepository employeeRepository;

    @Get
    public List<Employee> getAll(){

        return employeeRepository.findAll();

    }


    @Post
    public Integer save(@QueryValue String firstName, @QueryValue String lastName){

        return employeeRepository.save(firstName,lastName, new Date());
    }


}
