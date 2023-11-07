//package com.caijy.agent.tools.interceptor;
//
//import com.caijy.agent.core.plugin.interceptor.enhance.Enhancer;
//import net.bytebuddy.implementation.bind.annotation.AllArguments;
//import net.bytebuddy.implementation.bind.annotation.RuntimeType;
//import net.bytebuddy.implementation.bind.annotation.This;
//
///**
// * @author liguang
// * @date 2023/1/10 星期二 2:13 下午
// */
//public class ThreadConstructorInterceptor {
//
//    @RuntimeType
//    public void intercept(@This Object obj, @AllArguments Object[] allArguments) {
//        Enhancer enhancer = (Enhancer)obj;
//        enhancer.setDynamicField(Thread.currentThread());
//        System.out.println(
//            "ThreadConstructorInterceptor >>> " + obj + ",threadId:" + Thread.currentThread().getId() + ",threadName:"
//                + Thread.currentThread().getName());
//
//    }
//}
