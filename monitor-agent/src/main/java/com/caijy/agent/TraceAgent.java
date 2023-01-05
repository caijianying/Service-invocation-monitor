package com.caijy.agent;

import java.lang.instrument.Instrumentation;
import java.util.List;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginFinder;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import com.caijy.agent.utils.PluginUtil;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
public class TraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Config.init(agentArgs);

        try {
            AgentClassLoader.initDefaultLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PluginFinder pluginFinder = new PluginFinder(PluginUtil.loadPlugin());

        new Default()
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
            .type(pluginFinder.buildMatch())
            .transform((builder, type, classLoader, module) ->
            {
                try {
                    List<AbstractClassEnhancePluginDefine> enhancePluginDefines = pluginFinder.find(type);
                    for (AbstractClassEnhancePluginDefine pluginDefine : enhancePluginDefines) {
                        InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = pluginDefine
                            .getInstanceMethodsInterceptPoints();
                        for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint :
                            instanceMethodsInterceptPoints) {
                            ElementMatcher<MethodDescription> methodsMatcher = instanceMethodsInterceptPoint
                                .getMethodsMatcher();
                            if (methodsMatcher == null) {
                                methodsMatcher = ElementMatchers.any();
                            }
                            AgentClassLoader agentClassLoader = new AgentClassLoader(
                                InstrumentInterceptor.class.getClassLoader());
                            Object newInstance = Class.forName(instanceMethodsInterceptPoint.getMethodsInterceptor(),
                                true,
                                agentClassLoader).newInstance();
                            Junction<MethodDescription> junction = not(isStatic()).and(isPublic());
                            builder = builder.method(junction.and(methodsMatcher)).intercept(
                                MethodDelegation.to(new InstrumentInterceptor((MethodAroundInterceptor)newInstance)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return builder;

            })
            .installOn(inst);
    }
}
