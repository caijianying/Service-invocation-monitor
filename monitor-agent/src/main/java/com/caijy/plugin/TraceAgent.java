package com.caijy.plugin;

import java.lang.instrument.Instrumentation;

import com.caijy.plugin.inteceptor.SpringAnnotationInteceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
public class TraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("receive the package ：" + agentArgs);
        String packageName = agentArgs;

        new AgentBuilder.Default()
            .type(nameStartsWith(packageName).and(ElementMatchers.isAnnotatedWith(named("org.springframework.stereotype.Service").or(named("org.springframework.web.bind.annotation.RestController")))))
            .transform((builder, type, classLoader, module) ->
                builder.method(ElementMatchers.not(isStatic()).and(isPublic()).and(ElementMatchers.any()))
                    .intercept(MethodDelegation.to(SpringAnnotationInteceptor.class)))
            .installOn(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {

    }
}
