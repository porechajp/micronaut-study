package com.tnt.dynamo.services;


import com.tnt.dynamo.aspects.Stub;

@Stub
public interface StubService {


    @Stub("Hello")
    String sayHello(String name);


    @Stub("Hi")
    String sayHi(String name);
}
