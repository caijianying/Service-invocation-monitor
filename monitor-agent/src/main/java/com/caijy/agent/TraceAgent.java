package com.caijy.agent;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginFinder;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import com.caijy.agent.utils.PluginUtil;

import java.lang.instrument.Instrumentation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
@Slf4j
public class TraceAgent {

    public static void premain(String agentArgs, Instrumentation inst) {

        Config.init(agentArgs);
        try {
            AgentClassLoader.initDefaultLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PluginFinder pluginFinder = new PluginFinder(PluginUtil.loadPlugin());
        (new AgentBuilder.Default())
                .ignore(
                        nameStartsWith("net.bytebuddy.")
                                .or(nameStartsWith("org.slf4j."))
                                .or(nameStartsWith("org.groovy."))
                                .or(nameContains("javassist"))
                                .or(nameContains(".asm."))
                                .or(nameContains(".reflectasm."))
                                .or(nameStartsWith("sun.reflect"))
                                .or(isSynthetic()))

                .type(pluginFinder.buildMatch())
                .transform((builder, type, classLoader, module) -> {
                    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition receiverTypeDefinition = null;
                    try {
                        List<AbstractClassEnhancePluginDefine> enhancePluginDefines = pluginFinder.find(type);
                        for (AbstractClassEnhancePluginDefine pluginDefine : enhancePluginDefines) {
                            InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = pluginDefine.getInstanceMethodsInterceptPoints();
                            for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint : instanceMethodsInterceptPoints) {
                                ElementMatcher.Junction junction1 = null;
                                ElementMatcher<MethodDescription> methodsMatcher = instanceMethodsInterceptPoint.getMethodsMatcher();
                                if (methodsMatcher == null) {
                                    junction1 = any();
                                }
                                AgentClassLoader agentClassLoader = new AgentClassLoader(InstrumentInterceptor.class.getClassLoader());
                                Object newInstance = Class.forName(instanceMethodsInterceptPoint.getMethodsInterceptor(), true, agentClassLoader).newInstance();
                                ElementMatcher.Junction<MethodDescription> junction = not((ElementMatcher) isStatic()).and(isPublic());
                                receiverTypeDefinition = builder.method(junction.and(junction1)).intercept(MethodDelegation.to(new InstrumentInterceptor((MethodAroundInterceptor) newInstance)));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return receiverTypeDefinition;
                }).installOn(inst);
    }

}
