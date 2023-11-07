//package com.caijy.agent.tools.interceptor;
//
//import java.lang.reflect.Method;
//
//import com.caijy.agent.core.plugin.interceptor.enhance.Enhancer;
//import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
//import com.caijy.agent.core.plugin.interceptor.enhance.MethodInvocationContext;
//import com.caijy.agent.core.trace.ComponentDefine;
//import com.caijy.agent.tools.DemoAdvice;
//
///**
// * @author liguang
// * @date 2023/1/13 星期五 3:59 下午
// */
//public class ThreadInterceptor implements MethodAroundInterceptor {
//    @Override
//    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
//        Class<?>[] argumentsTypes, MethodInvocationContext context) throws Throwable {
//        Enhancer enhancer = (Enhancer)obj;
//        Thread parentThread = (Thread)enhancer.getDynamicField();
//        System.out.println("ThreadInterceptor.enter >>> parentThread = " + parentThread.getName() + ", " + clazz + ", " + method);
//        context.start(ThreadInterceptor.class.getSimpleName(), ComponentDefine.JDK_THREAD,
//            clazz.getName() + "." + method.getName());
//    }
//
//    @Override
//    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
//        MethodInvocationContext context) throws Throwable {
//        Enhancer enhancer = (Enhancer)obj;
//        Thread parentThread = (Thread)enhancer.getDynamicField();
//        System.out.println("ThreadInterceptor.exit >>> parentThread = " + parentThread.getName() + ", " + clazz + ", " + method);
//    }
//}
