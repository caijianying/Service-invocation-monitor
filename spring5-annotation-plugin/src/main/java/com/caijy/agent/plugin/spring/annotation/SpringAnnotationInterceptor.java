package com.caijy.agent.plugin.spring.annotation;

import java.lang.reflect.Method;

import com.caijy.agent.core.log.LogFactory;
import com.caijy.agent.core.log.Logger;
import com.caijy.agent.core.plugin.context.ContextManager;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.caijy.agent.core.trace.ComponentDefine;

/**
 * @author liguang
 * @date 2022/12/21 星期三 3:57 下午
 */
public class SpringAnnotationInterceptor implements MethodAroundInterceptorV1 {

    public static final Logger LOGGER = LogFactory.getLogger(SpringAnnotationInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes) {
        System.out.println("SpringAnnotationInterceptor.enter: " + clazz.getName() + "." + method.getName());
        String methodName = clazz.getName() + "." + method.getName();
        ContextManager.createSpan(ComponentDefine.SPRING, methodName);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.stopSpan();
    }

}
