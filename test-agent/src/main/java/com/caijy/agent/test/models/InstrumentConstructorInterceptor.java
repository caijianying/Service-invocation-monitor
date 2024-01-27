package com.caijy.agent.test.models;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author caijy
 * @description
 * @date 2024/1/2 星期二 4:48 下午
 */
@Slf4j
public class InstrumentConstructorInterceptor implements MonitorInterceptor {

    private ConstructorInterceptorV1 interceptorV1;

    public InstrumentConstructorInterceptor(ConstructorInterceptorV1 interceptorV1) {
        this.interceptorV1 = interceptorV1;
    }

    @RuntimeType
    public void intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments) throws Throwable {
        interceptorV1.intercept(obj,clazz,allArguments,context);
    }
}
