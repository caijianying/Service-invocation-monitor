package com.caijy.test.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.This;

/**
 * @author liguang
 * @date 2023/1/11 星期三 3:30 下午
 */
public class DemoAdvice {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin String method, @AllArguments Object args, @This Object obj) {
        System.out.println("Invoke method OnMethodEnter,method: " + method + "args: " + args + ",obj: " + obj);
    }

    @Advice.OnMethodExit
    public static void after(@Advice.Origin String method) {
        System.out.println("Invoke method OnMethodExit " + method);
    }
}
