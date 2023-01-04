package com.caijy.agent.plugin.dubbo;

import java.lang.reflect.Method;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.log.LogFactory;
import com.caijy.agent.core.log.Logger;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodInvocationContext;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.core.utils.IgnoredUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;

/**
 * @author liguang
 * @date 2022/12/16 星期五 3:54 下午
 */
public class DubboInterceptor implements MethodAroundInterceptor {

    public static final Logger LOGGER = LogFactory.getLogger(DubboInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        Invoker invoker = (Invoker)allArguments[0];
        Invocation invocation = (Invocation)allArguments[1];
        RpcContext rpcContext = RpcContext.getContext();
        boolean isConsumer = rpcContext.isConsumerSide();
        URL requestURL = invoker.getUrl();

        if (IgnoredUtils.ignoredSpecificSymbol(invocation.getMethodName())) {
            return;
        }

        // XXGroup/com.alibaba.cloud.dubbo.service.DubboMetadataService.$invoke(String,String[],Object[])
        if (this.checkIsGroup(requestURL)){
            context.setContinue(false);
            return;
        }

        String methodName = generateOperationName(requestURL, invocation);

        LOGGER.debug("beforeMethod >> isConsumer:%s", isConsumer);
        LOGGER.debug("beforeMethod >> method:%s", generateOperationName(requestURL, invocation));
        LOGGER.debug("beforeMethod >> generateRequestURL:%s", generateRequestURL(requestURL, invocation));
        LOGGER.debug("beforeMethod >> Arguments:%s", this.buildArgs(invocation));

        context.start(this.getClass().getSimpleName(), ComponentDefine.DUBBO, methodName);
    }

    @Override
    public boolean isInvalid(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes) {
        Invoker invoker = (Invoker)allArguments[0];
        URL requestURL = invoker.getUrl();
        String groupStr = requestURL.getParameter(Constants.GROUP_KEY);
        return StrUtil.isNotEmpty(groupStr);
    }

    private boolean checkIsGroup(URL requestURL) {
        String groupStr = requestURL.getParameter(Constants.GROUP_KEY);
        return StrUtil.isNotEmpty(groupStr);
    }

    private String buildArgs(Invocation invocation) {
        int argumentsLengthThreshold = 256;
        Object[] parameters = invocation.getArguments();
        if (parameters != null && parameters.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean first = true;
            for (Object parameter : parameters) {
                if (!first) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(parameter);
                if (stringBuilder.length() > argumentsLengthThreshold) {
                    stringBuilder.append("...");
                    break;
                }
                first = false;
            }
            return stringBuilder.toString();
        }
        return "";
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

    private String generateRequestURL(URL url, Invocation invocation) {
        StringBuilder requestURL = new StringBuilder();
        requestURL.append(url.getProtocol() + "://");
        requestURL.append(url.getHost());
        requestURL.append(":" + url.getPort() + "/");
        requestURL.append(generateOperationName(url, invocation));
        return requestURL.toString();
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        Invoker invoker = (Invoker)allArguments[0];
        Invocation invocation = (Invocation)allArguments[1];
        URL requestURL = invoker.getUrl();
        String methodName = generateOperationName(requestURL, invocation);
        context.stop(this.getClass().getSimpleName(), ComponentDefine.DUBBO, methodName);
    }

}
