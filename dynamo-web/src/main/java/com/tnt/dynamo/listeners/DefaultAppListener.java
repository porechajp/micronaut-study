package com.tnt.dynamo.listeners;


import com.tnt.dynamo.entities.Identified;
import com.tnt.dynamo.repositories.DynamoRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;

@Requires(property = "dynamodb-local.host")
@Requires(property = "dynamodb-local.port")
@Requires(env = Environment.DEVELOPMENT)
@Singleton
public class DefaultAppListener implements ApplicationEventListener<StartupEvent> {


    private final DynamoRepository<? extends Identified> dynamoRepository;

    public DefaultAppListener(DynamoRepository<? extends Identified> dynamoRepository) {
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        if (!dynamoRepository.existsTable()) {
            dynamoRepository.createTable();
        }
    }
}
