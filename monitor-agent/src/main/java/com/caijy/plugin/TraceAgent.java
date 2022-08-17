package com.caijy.plugin;

import java.lang.instrument.Instrumentation;
import com.caijy.plugin.advice.TraceAdvice;
import com.caijy.plugin.inteceptor.SpringAnnotationInteceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

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
        System.out.println("基于javaagent链路追踪 => agentmain");

        AgentBuilder agentBuilder = new AgentBuilder.Default();

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
            builder = builder.visit(
                Advice.to(TraceAdvice.class)
                    .on(ElementMatchers.isMethod()
                        .and(ElementMatchers.any()).and(ElementMatchers.not(ElementMatchers.nameStartsWith("main")))));
            return builder;
        };

        //agentBuilder = agentBuilder.type(ElementMatchers.nameStartsWith("org.itstack.demo.test")).transform
        // (transformer).asDecorator();
        agentBuilder.type(ElementMatchers.nameStartsWith("com.attach")).transform(transformer).installOn(inst);

        //监听
        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader,
                JavaModule javaModule, boolean b, DynamicType dynamicType) {
                System.out.println("onTransformation：" + typeDescription);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule,
                boolean b) {

            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b,
                Throwable throwable) {

            }

            @Override
            public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

            }

        };

        agentBuilder.with(listener).installOn(inst);
    }
}
