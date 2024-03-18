package com.tnt.dynamo.repositories;

import jakarta.inject.Singleton;

import java.util.concurrent.atomic.AtomicInteger;


@Singleton
public class IdGenerator {

    private static final AtomicInteger genrator = new AtomicInteger(10);

    public Integer generate(){
        return genrator.getAndIncrement();
    }



}
