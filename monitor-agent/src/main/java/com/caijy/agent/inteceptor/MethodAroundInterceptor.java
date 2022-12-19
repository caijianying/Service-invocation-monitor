package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:45 下午
 */
public interface MethodAroundInterceptor {

    void beforeMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context)
        throws Throwable;

    void afterMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context)
        throws Throwable;

}
