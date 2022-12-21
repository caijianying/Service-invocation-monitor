package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;

import com.alibaba.fastjson.JSON;

/**
 * @author liguang
 * @date 2022/12/16 星期五 3:54 下午
 */
public class DubboInterceptor implements MethodAroundInterceptor {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        //Invoker invoker = (Invoker) allArguments[0];
        //Invocation invocation = (Invocation) allArguments[1];
        //RpcContext rpcContext = RpcContext.getContext();
        //boolean isConsumer = rpcContext.isConsumerSide();
        //URL requestURL = invoker.getUrl();

        //System.out.println("DubboInterceptor.beforeMethod >> method: " + JSON.toJSONString(method));
        //System.out.println("DubboInterceptor.beforeMethod >> allArguments: " + allArguments);
        //System.out.println("DubboInterceptor.beforeMethod >> argumentsTypes: " + argumentsTypes);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        //System.out.println("DubboInterceptor.afterMethod >> method: " + JSON.toJSONString(method));
        //System.out.println("DubboInterceptor.afterMethod >> allArguments: " + allArguments);
        //System.out.println("DubboInterceptor.afterMethod >> argumentsTypes: " + argumentsTypes);
    }
}
