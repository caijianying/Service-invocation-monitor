package com.caijy.agent.test;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.plugin.context.ContextManager;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.core.utils.IgnoredUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liguang
 * @date 2022/12/16 星期五 3:54 下午
 */
@Slf4j
public class TestDubboInterceptorV1 {


    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
                            @Origin Method method,
                            @SuperCall Callable<?> callable) throws Throwable {

        System.out.println("TestDubboInterceptorV1.intercept:" + clazz.getName());
        return callable.call();
    }

    /**
     * @return operation name.
     */
    private String generateOperationName(URL requestURL, Invocation invocation) {
        StringBuilder operationName = new StringBuilder();
        String groupStr = requestURL.getParameter(Constants.GROUP_KEY);
        groupStr = StrUtil.isEmpty(groupStr) ? "" : groupStr + "/";
        operationName.append(groupStr);
        operationName.append(requestURL.getPath());
        operationName.append("." + invocation.getMethodName() + "(");
        for (Class<?> classes : invocation.getParameterTypes()) {
            operationName.append(classes.getSimpleName() + ",");
        }

        if (invocation.getParameterTypes().length > 0) {
            operationName.delete(operationName.length() - 1, operationName.length());
        }

        operationName.append(")");

        return operationName.toString();
    }

    public boolean isInvalid(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        System.out.println("DubboInterceptorV1.isInvalid:" + clazz.getName());
        Invoker invoker = (Invoker) allArguments[0];
        URL requestURL = invoker.getUrl();
        String groupStr = requestURL.getParameter(Constants.GROUP_KEY);
        return StrUtil.isNotEmpty(groupStr);
    }
}
