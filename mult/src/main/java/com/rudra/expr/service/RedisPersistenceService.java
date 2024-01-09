package com.rudra.expr.service;


import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class RedisPersistenceService {

    @Inject
    StatefulRedisConnection<String, String> connection;


    public void persist(String name){
        var cmds = connection.sync();

        cmds.lpush("UserNames",name);
    }

    public List<String> readAll(){
        var cmds = connection.sync();

        return cmds.lrange("UserNames",0,-1);

    }
}
