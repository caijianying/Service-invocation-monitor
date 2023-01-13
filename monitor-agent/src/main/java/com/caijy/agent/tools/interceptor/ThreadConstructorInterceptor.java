package com.caijy.agent.tools.interceptor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author liguang
 * @date 2023/1/10 星期二 2:13 下午
 */
public class ThreadConstructorInterceptor {

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments) {

        System.out.println("ThreadConstructorInterceptor >>> " + obj);

    }
}
