package com.caijy.agent.plugin.jdk.thread;

import com.caijy.agent.core.plugin.context.ContextManager;
import com.caijy.agent.core.plugin.context.ContextSnapshot;
import com.caijy.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.caijy.agent.core.trace.ComponentDefine;

import java.lang.reflect.Method;

/**
 * @author caijy
 * @description
 * @date 2024/1/25 星期四 7:55 下午
 */
public class JdkThreadMethodInterceptor implements MethodAroundInterceptorV1 {
    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        System.out.println("JdkThreadMethodInterceptor>:: " + obj.getClass().getName() + "." + method.getName());
        ContextSnapshot snapshot = (ContextSnapshot) ((EnhancedInstance) obj).getDynamicField();
        String url = clazz.getName() + "." + method.getName();
        ContextManager.createSpan( ComponentDefine.JDK_THREAD, url, snapshot);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        ContextManager.stopSpan();
    }
}
