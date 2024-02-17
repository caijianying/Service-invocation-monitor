package com.caijy.agent.test;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentMethodInterceptor;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
@Slf4j
public class TestTraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {

//        Config.init(agentArgs);
//        try {
//            AgentClassLoader.initDefaultLoader();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ElementMatcher.Junction judge =  ElementMatchers.not(isInterface()).and(named("org.apache.dubbo.monitor.support.MonitorFilter"));

        (new AgentBuilder.Default())
                .ignore(
                        nameStartsWith("net.bytebuddy.")
                                .or(nameStartsWith("org.slf4j."))
                                .or(nameStartsWith("org.groovy."))
                                .or(nameContains("javassist"))
                                .or(nameContains(".asm."))
                                .or(nameContains(".reflectasm."))
                                .or(nameStartsWith("sun.reflect"))
//                                .or(nameStartsWith("org.springframework"))
//                                .or(nameStartsWith("com.intellij.rt.execution"))
                                .or(isSynthetic()))
                .type(judge)
                .transform((builder, type, classLoader, module) -> {
                    try{
                        return builder.method(not(isStatic()).and(named("invoke")))
                                .intercept(MethodDelegation.withDefaultConfiguration().to(TestDubboInterceptorV1.class));
                    }finally {
                        return builder;
                    }
                })
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .installOn(inst);
    }
}
