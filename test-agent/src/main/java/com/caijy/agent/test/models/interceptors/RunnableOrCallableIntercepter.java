package com.caijy.agent.test.models.interceptors;

import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.test.models.InvocationContextV1;
import com.caijy.agent.test.models.MethodAroundInterceptorV1;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author caijy
 * @description
 * @date 2023/11/4 星期六 3:39 下午
 */
@Slf4j
public class RunnableOrCallableIntercepter implements MethodAroundInterceptorV1 {
    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes, InvocationContextV1 context) throws Throwable {
        if (obj instanceof Runnable || obj instanceof Callable) {
            log.info("RunnableOrCallableIntercepter,obj = {},clazz={},method={}", obj, clazz.getName(),
                    method.getName());
            String url = clazz.getName() + "." + method.getName();
            log.info("url:{} .enter", url);
            context.enter(ComponentDefine.JDK_THREAD, url);
        }
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes, InvocationContextV1 context) throws Throwable {
        if (obj instanceof Runnable || obj instanceof Callable) {
            log.info("RunnableOrCallableIntercepter,obj = {},clazz={},method={}", obj, clazz.getName(),
                    method.getName());
            String url = clazz.getName() + "." + method.getName();
            log.info("url:{} .exit", url);
            context.exit();
        }
    }
}
