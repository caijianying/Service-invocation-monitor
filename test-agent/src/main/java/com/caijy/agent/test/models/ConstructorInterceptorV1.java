package com.caijy.agent.test.models;

/**
 * @author caijy
 * @description
 * @date 2024/1/2 星期二 5:07 下午
 */
public interface ConstructorInterceptorV1 {

    void intercept(Object obj, Class<?> clazz, Object[] allArguments, InvocationContextV1 context);

}
