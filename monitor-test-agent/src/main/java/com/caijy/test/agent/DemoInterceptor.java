package com.caijy.test.agent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author liguang
 * @date 2023/1/10 星期二 2:13 下午
 */
public class DemoInterceptor {

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
        @Origin Method method,
        @SuperCall Callable<?> callable) throws Throwable {

        System.out.println("DemoInterceptor >>> " + obj + ", " + clazz + ", " + method);

        return callable.call();
    }
}
