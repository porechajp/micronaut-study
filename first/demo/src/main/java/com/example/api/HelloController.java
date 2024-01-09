package com.example.api;

import java.util.Optional;
import java.util.stream.Stream;

import com.example.entities.Person;
import com.example.service.PersonRegistry;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/hello")
public class HelloController {
	
	@Inject
	private PersonRegistry registry;
	
	
	@Post(uri="/register", consumes = "application/json", produces="text/plain")
	public int register(@Body Person person) {
		
		return registry.register(person).id();
		
	}
	
	@Get(uri="/", produces="application/json")	
	public Stream<Person> list(){
		return registry.listAll();
	}
	
	@Get(uri="/{id}", produces="application/json")
	public Optional<Person> get(int id){
		
		return registry.get(id);		
	}

}
