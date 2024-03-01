package com.caijy.agent.plugin.dubbo;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.plugin.context.ContextManager;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.caijy.agent.core.trace.ComponentDefine;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import java.lang.reflect.Method;

/**
 * @author liguang
 * @date 2022/12/16 星期五 3:54 下午
 */
@Slf4j
public class DubboInterceptor implements MethodAroundInterceptorV1 {
    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
//        log.debug("DubboInterceptorV1.enter:{}", clazz.getName());
        Invoker invoker = (Invoker) allArguments[0];
        Invocation invocation = (Invocation) allArguments[1];
        boolean isConsumer = isConsumerSide(invocation);
        URL requestURL = invoker.getUrl();
        String methodName = generateOperationName(requestURL, invocation);
//        log.debug("methodName:{},isConsumer:{}", methodName, isConsumer);
        ContextManager.createSpan(ComponentDefine.DUBBO, methodName);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        ContextManager.stopSpan();
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

    private boolean isConsumerSide(Invocation invocation) {
        Invoker<?> invoker = invocation.getInvoker();
        // As RpcServiceContext may not been reset when it's role switched from provider
        // to consumer in the same thread, but RpcInvocation is always correctly bounded
        // to the current request or serve request, https://github.com/apache/skywalking-java/pull/110
        return invoker.getUrl()
                .getParameter("side", "provider")
                .equals("consumer");
    }
}
