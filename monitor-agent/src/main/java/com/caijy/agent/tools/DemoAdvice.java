package com.caijy.agent.tools;

import java.lang.reflect.Method;

import com.caijy.agent.core.plugin.interceptor.enhance.MethodInvocationContext;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.tools.thread.ThreadSpanHandler;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.This;

/**
 * @author liguang
 * @date 2023/1/11 星期三 3:30 下午
 */
public class DemoAdvice {

    public static MethodInvocationContext context = new MethodInvocationContext();

    private static ThreadSpanHandler handler = new ThreadSpanHandler();

    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin Method method, @Advice.Origin Class<?> clazz, @AllArguments Object args,
        @This Object obj) {
        System.out.println(
            "DemoAdvice >>: " + clazz.getName() + "." + method.getName() + "，thread[" + Thread.currentThread().getId()
                + ":" + Thread.currentThread().getName() + "]" + ".enter");
        //context.start(DemoAdvice.class.getSimpleName(), ComponentDefine.JDK_THREAD,
        //    clazz.getName() + "." + method.getName());
    }

    @Advice.OnMethodExit
    public static void after(@Advice.Origin Method method, @Advice.Origin Class<?> clazz, @AllArguments Object args,
        @This Object obj) {
        System.out.println(
            "DemoAdvice >>: " + clazz.getName() + "." + method.getName() + "，thread[" + Thread.currentThread().getId()
                + ":" + Thread.currentThread().getName() + "]" + ".exit");
        //context.stop(DemoAdvice.class.getSimpleName(), ComponentDefine.JDK_THREAD,
        //    clazz.getName() + "." + method.getName());
    }
}
