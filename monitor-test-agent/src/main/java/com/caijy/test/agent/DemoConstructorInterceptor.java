package com.caijy.test.agent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liguang
 * @date 2023/1/10 星期二 2:13 下午
 */
public class DemoConstructorInterceptor {

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments) {

        System.out.println("DemoConstructorInterceptor >>> " + obj);

    }
}
