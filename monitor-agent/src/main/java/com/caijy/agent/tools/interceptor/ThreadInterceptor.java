package com.caijy.agent.tools.interceptor;

import java.lang.reflect.Method;

import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodInvocationContext;

/**
 * @author liguang
 * @date 2023/1/13 星期五 3:59 下午
 */
public class ThreadInterceptor implements MethodAroundInterceptor {
    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes, MethodInvocationContext context) throws Throwable {
        System.out.println("ThreadInterceptor.enter >>> " + obj + ", " + clazz + ", " + method);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        System.out.println("ThreadInterceptor.exit >>> " + obj + ", " + clazz + ", " + method);
    }
}
