package com.tnt.dynamo.aspects;


import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Introduction // (1)
@Bean // (2)
@Documented
@Retention(RUNTIME)
@Target({METHOD, ANNOTATION_TYPE, TYPE})
public @interface Stub {
    String value() default "";
}
