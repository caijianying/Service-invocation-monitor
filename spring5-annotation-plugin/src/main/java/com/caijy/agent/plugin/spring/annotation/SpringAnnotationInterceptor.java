package com.caijy.agent.plugin.spring.annotation;

import java.lang.reflect.Method;

import com.caijy.agent.core.log.LogFactory;
import com.caijy.agent.core.log.Logger;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodInvocationContext;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.core.utils.IgnoredUtils;

/**
 * @author liguang
 * @date 2022/12/21 星期三 3:57 下午
 */
public class SpringAnnotationInterceptor implements MethodAroundInterceptor {

    public static final Logger LOGGER = LogFactory.getLogger(SpringAnnotationInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        if (IgnoredUtils.ignoredMethods(method.getName())) {
            return;
        }
        String methodName = clazz.getName() + "." + method.getName();
        context.start(this.getClass().getSimpleName(), ComponentDefine.SPRING, methodName);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        String methodName = clazz.getName() + "." + method.getName();
        context.stop(this.getClass().getSimpleName(), ComponentDefine.SPRING, methodName);

    }

}
