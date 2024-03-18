package com.tnt.dynamo.aspects.introductions;


import com.tnt.dynamo.aspects.Stub;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import jakarta.inject.Singleton;

@Singleton
@InterceptorBean(Stub.class)
public class StubIntroduction implements MethodInterceptor<Object,Object> {

    @Override
    public @Nullable Object intercept(MethodInvocationContext<Object, Object> context) {


        var greet = context.getValue(Stub.class, Argument.STRING).orElseThrow();

        var args = context.getParameterValues();

        greet = args.length == 1 ? greet + " " + args[0].toString() : greet;

        return greet;
    }
}
