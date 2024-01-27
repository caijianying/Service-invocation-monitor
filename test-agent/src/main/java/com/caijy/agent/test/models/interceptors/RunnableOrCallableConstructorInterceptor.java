package com.caijy.agent.test.models.interceptors;

import com.caijy.agent.test.models.ConstructorInterceptorV1;
import com.caijy.agent.test.models.ContextSnapshot;
import com.caijy.agent.test.models.InvocationContextV1;

import java.util.concurrent.Callable;

/**
 * @author caijy
 * @description
 * @date 2024/1/2 星期二 5:14 下午
 */
public class RunnableOrCallableConstructorInterceptor implements ConstructorInterceptorV1 {

    @Override
    public void intercept(Object obj, Class<?> clazz, Object[] allArguments, InvocationContextV1 context) {
        if (obj instanceof Runnable || obj instanceof Callable) {
            context.prepareCrossThread();
        }
    }
}
