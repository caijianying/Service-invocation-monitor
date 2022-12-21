package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Callable;

import cn.hutool.core.collection.CollectionUtil;
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

    private List<MethodAroundInterceptor> interceptors;

    public InstrumentInterceptor(List<MethodAroundInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
        @Origin Method method,
        @SuperCall Callable<?> callable) throws Throwable {
        System.err.println(clazz.getName() + "." + method.getName() + ">>> enter.");
        MethodInvocationContext context = new MethodInvocationContext();
        if (CollectionUtil.isNotEmpty(interceptors)) {
            for (MethodAroundInterceptor interceptor : interceptors) {
                try {
                    interceptor.beforeMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
                } catch (Throwable e) {
                    System.out.println(
                        "Service Invocation Monitor | ERROR: before method invoke An Error occurred! reason:" + e
                            .getMessage());
                    e.printStackTrace();
                } finally {
                    System.out.println(
                        "Service Invocation Monitor | SUCCESS: before method invoke ");
                }

            }
        }
        Object call = callable.call();
        System.err.println(clazz.getName() + "." + method.getName() + ">>> exit.");
        if (CollectionUtil.isNotEmpty(interceptors)) {
            for (MethodAroundInterceptor interceptor : interceptors) {
                try {
                    interceptor.afterMethod(obj, clazz, method, allArguments, method.getParameterTypes(), context);
                } catch (Throwable e) {
                    System.out.println(
                        "Service Invocation Monitor | ERROR: after method invoke An Error occurred! reason:" + e
                            .getMessage());
                    e.printStackTrace();
                } finally {
                    System.out.println(
                        "Service Invocation Monitor | SUCCESS: after method invoke ");
                }
            }
        }
        return call;
    }
}
