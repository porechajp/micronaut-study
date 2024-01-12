package com.rudra.exp.service;

import com.rudra.exp.entities.User;
import io.lettuce.core.api.StatefulRedisConnection;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Inject
    private ObjectMapper objMapper;


    @Inject
    private StatefulRedisConnection<String,String> conn;




    public List<User> readAll(){

        return conn.sync().hgetall("Users").values().stream().map(v -> {
            try {
                return objMapper.readValue(v, User.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();


        //return getUserStream().toList();
    }

    public Optional<User> find(int id){

        var data = conn.sync().hget("Users",String.valueOf(id));

        try {

            return Optional.ofNullable(StringUtils.isEmpty(data) ? null : objMapper.readValue(data, User.class));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Stream<User> getUserStream() {

        return conn.sync().lrange("Users", 0, -1).stream().map(s -> {
            try {
                return objMapper.readValue(s, User.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean update(User user){

        var userFound = find(user.id());

        if(userFound.isPresent()){
            create(user);
            return true;
        }

        return false;

    }

    public void create(User user) {
        try {

            logger.info("Calling Redis for "+user.toString());
            conn.sync().hset("Users", user.id().toString(),objMapper.writeValueAsString(user));
            logger.info("Inserted");


            //conn.sync().rpush("Users",objMapper.writeValueAsString(user));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
