package com.caijy.agent.test.models.interceptors;

import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.test.models.InvocationContextV1;
import com.caijy.agent.test.models.MethodAroundInterceptorV1;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author liguang
 * @date 2023/1/4 星期三 2:29 下午
 */
@Slf4j
public class MvcGetMappingInterceptor implements MethodAroundInterceptorV1 {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes,
                             InvocationContextV1 context) {
        String url = "";
        if (context.isRoot()) {
            url = clazz.getName() + "." + method.getName();
        }
        if (url.isEmpty()) {
            url = clazz.getName() + "." + method.getName();
            context.enter(ComponentDefine.SPRING, url);
            return;
        }
        context.enter(ComponentDefine.MVC, url);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                            InvocationContextV1 context) {
        context.exit();
    }
}