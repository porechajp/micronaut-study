package com.tnt.dynamo.entities;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Date;


@Serdeable
@Introspected
public record Employee(Integer id, String firstName, String lastName, Date joining) implements  Identified {
}
