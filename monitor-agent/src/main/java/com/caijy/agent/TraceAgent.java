package com.caijy.agent;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.context.Config;
import com.caijy.agent.inteceptor.DubboInterceptor;
import com.caijy.agent.inteceptor.InstrumentInterceptor;
import com.caijy.agent.inteceptor.SpringAnnotationInteceptor;
import com.caijy.agent.inteceptor.SpringInterceptor;
import com.google.common.collect.Lists;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
public class TraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Config.init(agentArgs);
        Object configValue = Config.get(AgentConstant.MONITOR_PACKAGE);
        String packageName = Objects.isNull(configValue) ? null : configValue.toString();

        //Junction<? super TypeDescription> junction = ElementMatchers.isAnnotatedWith(
        //    named("org.springframework.stereotype.Service")
        //        .or(named("org.springframework.web.bind.annotation.RestController")));
        //
        //if (StrUtil.isNotBlank(packageName)) {
        //    System.out.println("receive the package ：" + packageName);
        //    junction = junction.and(nameStartsWith(packageName));
        //}

        Junction<? super TypeDescription> judge = ElementMatchers.isAnnotatedWith(
            named("org.springframework.stereotype.Service")
                .or(named("org.springframework.web.bind.annotation.RestController")));
        judge = judge.and(not(isInterface()));
        Junction<? super TypeDescription> junction = judge.or(
            ElementMatchers.named("com.alibaba.dubbo.monitor.support.MonitorFilter")
                .or(named("org.apache.dubbo.monitor.support.MonitorFilter")));

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
                    .intercept(MethodDelegation.to(new InstrumentInterceptor(
                        Lists.newArrayList(new DubboInterceptor(), new SpringInterceptor())))))
            .installOn(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {

    }

    //private static class Transformer implements AgentBuilder.Transformer {
    //
    //    @Override
    //    public DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder,
    //        final TypeDescription typeDescription,
    //        final ClassLoader classLoader,
    //        final JavaModule module) {
    //
    //        builder.method(
    //            ElementMatchers.not(isStatic()).and(ElementMatchers.named("invoke").or(named("makeWrapper"))))
    //            .intercept(MethodDelegation.to(new InstrumentInterceptor(new DubboInterceptor())));
    //        return builder;
    //    }
    //}
}
