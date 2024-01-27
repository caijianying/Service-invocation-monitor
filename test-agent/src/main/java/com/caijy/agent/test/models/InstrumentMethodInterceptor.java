package com.caijy.agent.test.models;

import com.caijy.agent.core.utils.IgnoredUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:43 下午
 */
@Slf4j
public class InstrumentMethodInterceptor implements MonitorInterceptor {


    private MethodAroundInterceptorV1 interceptor;

    public InstrumentMethodInterceptor(MethodAroundInterceptorV1 interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
                            @Origin Method method,
                            @SuperCall Callable<?> callable) throws Throwable {

        if (IgnoredUtils.ignoredMethods(method.getName())) {
            return callable.call();
        }

        if (interceptor.isInvalid(obj, clazz, method, allArguments, method.getParameterTypes())) {
            return callable.call();
        }
        try {
            interceptor.beforeMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            log.error(
                    "Service Invocation Monitor | before method invoke An Error occurred! from interceptor:{},"
                            + "reason:{}",
                    interceptor.getClass().getSimpleName(), e
                            .getMessage(), e);
        }
        Object call = null;
        try {
            call = callable.call();
        } catch (Throwable e) {
            throw e;
        }

        try {
            interceptor.afterMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            log.error(
                    "Service Invocation Monitor | after method invoke An Error occurred! from interceptor:{},"
                            + "reason:{}",
                    interceptor.getClass().getSimpleName(), e
                            .getMessage(), e);
        }
        return call;
    }
}
