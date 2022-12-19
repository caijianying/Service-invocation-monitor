package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Callable;

import com.caijy.agent.context.TrackManager;
import com.caijy.agent.model.TraceSegmentModel;
import com.caijy.agent.utils.TraceSegmentBuilder;
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

    private MethodAroundInterceptor interceptor;

    public InstrumentInterceptor(MethodAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
        @Origin Method method,
        @SuperCall Callable<?> callable) throws Throwable {

        MethodInvocationContext context = new MethodInvocationContext();
        try {
            this.interceptor.beforeMethod(obj, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            System.out.println(
                "Service Invocation Monitor | ERROR: before method invoke An Error occurred! reason:" + e.getMessage());
        } finally {
            System.out.println(
                "Service Invocation Monitor | SUCCESS: before method invoke ");
        }

        Object call = callable.call();

        try {
            this.interceptor.afterMethod(obj, method, allArguments, method.getParameterTypes(), context);
        } catch (Throwable e) {
            System.out.println(
                "Service Invocation Monitor | ERROR: after method invoke An Error occurred! reason:" + e.getMessage());
        } finally {
            System.out.println(
                "Service Invocation Monitor | SUCCESS: after method invoke ");
        }

        return call;
    }
}
