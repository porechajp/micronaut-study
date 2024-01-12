package com.rudra.test.entities;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Date;
//import java.util.concurrent.atomic.AtomicInteger;


@Introspected
@Serdeable
public record User(Integer id, String firstName, String lastName, Date dob) {

/*
    private final static AtomicInteger idGenerator = new AtomicInteger(1);

    public User(String firstName, String lastName, Date dob){
        this(idGenerator.getAndIncrement(), firstName,lastName,dob);

    }

 */

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User that && that.id.equals(this.id);
    }
}
