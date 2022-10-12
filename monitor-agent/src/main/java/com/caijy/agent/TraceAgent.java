package com.caijy.agent;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.context.Config;
import com.caijy.agent.inteceptor.SpringAnnotationInteceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
public class TraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Config.init(agentArgs);
        Object configValue = Config.get(AgentConstant.MONITOR_PACKAGE);
        String packageName = Objects.isNull(configValue) ? null : configValue.toString();

        Junction<? super TypeDescription> junction = ElementMatchers.isAnnotatedWith(
            named("org.springframework.stereotype.Service")
                .or(named("org.springframework.web.bind.annotation.RestController")));

        if (StrUtil.isNotBlank(packageName)) {
            System.out.println("receive the package ：" + packageName);
            junction = junction.and(nameStartsWith(packageName));
        }

        new AgentBuilder.Default()
            .ignore(
                nameStartsWith("net.bytebuddy.")
                    .or(nameStartsWith("org.slf4j."))
                    .or(nameStartsWith("org.groovy."))
                    .or(nameContains("javassist"))
                    .or(nameContains(".asm."))
                    .or(nameContains(".reflectasm."))
                    .or(nameStartsWith("sun.reflect"))
                    .or(ElementMatchers.isSynthetic())
            )
            .type(junction)
            .transform((builder, type, classLoader, module) ->
                builder.method(ElementMatchers.not(isStatic()).and(isPublic()).and(ElementMatchers.any()))
                    .intercept(MethodDelegation.to(SpringAnnotationInteceptor.class)))
            .installOn(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {

    }
}
