package com.example.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.example.entities.Person;

import jakarta.inject.Singleton;

@Singleton
public class PersonRegistry {
	
	private final AtomicInteger counter = new AtomicInteger(1);
	private final Map<Integer, Person> registry = new ConcurrentSkipListMap<Integer, Person>( (a,b)-> Integer.compare(a, b) ); 
	
	public Person register(Person person) {
		
		var newPerson = new Person(counter.getAndIncrement(), person.firstName(), person.lastName());
		
		registry.put(newPerson.id(), person);
		
		return newPerson;
		
	}

	public Stream<Person> listAll() {
		// TODO Auto-generated method stub
		return registry.values().stream();
	}

	public Optional<Person> get(int id) {
		
		return Optional.ofNullable(registry.get(id));
	}

}
