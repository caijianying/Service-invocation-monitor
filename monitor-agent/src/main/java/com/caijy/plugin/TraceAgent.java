package com.caijy.plugin;

import java.lang.instrument.Instrumentation;

import com.caijy.plugin.advice.TraceAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
public class TraceAgent {

    //JVM 首先尝试在代理类上调用以下方法
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("基于javaagent链路追踪 => premain");
        System.out.println("基于javaagent链路追踪,参数：" + agentArgs);
        AgentBuilder agentBuilder = new AgentBuilder.Default();
        String packageName = agentArgs;

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
            builder = builder.visit(
                Advice.to(TraceAdvice.class)
                    .on(ElementMatchers.isMethod()
                        .and(ElementMatchers.any()).and(ElementMatchers.not(ElementMatchers.nameStartsWith("main"))
                        )));
            return builder;
        };

        agentBuilder.type(ElementMatchers.nameStartsWith(packageName)).transform(transformer).installOn(inst);

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
