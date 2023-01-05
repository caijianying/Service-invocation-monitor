package com.caijy.agent.core.plugin.interceptor.enhance;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import com.caijy.agent.core.utils.IgnoredUtils;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:43 下午
 */
public class InstrumentInterceptor {

    private List<MethodAroundInterceptor> interceptors;

    private MethodAroundInterceptor interceptor;

    public InstrumentInterceptor(List<MethodAroundInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public InstrumentInterceptor(MethodAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
        @Origin Method method,
        @SuperCall Callable<?> callable) throws Throwable {

        if (IgnoredUtils.ignoredMethods(method.getName())) {
            return callable.call();
        }

        if (interceptor.isInvalid(obj, clazz, method, allArguments, method.getParameterTypes())){
            return callable.call();
        }
        MethodInvocationContext context = new MethodInvocationContext();
        try {
            interceptor.beforeMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            System.out.println(String.format(
                "Service Invocation Monitor | ERROR: before method invoke An Error occurred! from interceptor: %s,"
                    + "reason:%s",
                interceptor.getClass().getSimpleName(), e
                    .getMessage()));
        }
        Object call = null;
        try {
            call = callable.call();
        } catch (Throwable e) {
            context.setContinue(false);
            throw e;
        }

        if (!context.isContinue()) {
            context.clear();
            return call;
        }

        try {
            interceptor.afterMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            System.out.println(String.format(
                "Service Invocation Monitor | ERROR: after method invoke An Error occurred! from interceptor: %s,"
                    + "reason:%s",
                interceptor.getClass().getSimpleName(), e
                    .getMessage()));
        }
        return call;
    }
}
