package com.caijy.agent.test;

import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * @author caijy
 * @description
 * @date 2024/2/17 星期六 3:25 下午
 */
@Slf4j
public class SimpleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        log.debug("SimpleAgent....premain");
        new AgentBuilder.Default()
                .type(ElementMatchers.named("org.apache.dubbo.monitor.support.MonitorFilter"))
                .transform((builder, type, classLoader, module) ->
                        builder.method(ElementMatchers.named("invoke"))
                                .intercept(MethodDelegation.to(new InstrumentMethodInterceptor(new DubboInterceptorV1()))))
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .installOn(inst);
    }
}
