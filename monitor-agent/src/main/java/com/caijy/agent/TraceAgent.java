package com.caijy.agent;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginFinder;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import com.caijy.agent.utils.PluginUtil;

import java.lang.instrument.Instrumentation;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;

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
                                .or(nameStartsWith("org.apache"))
                                .or(nameStartsWith("org.springframework"))
                                .or(nameStartsWith("com.intellij.rt.execution"))
                                .or(isSynthetic()))
                .type(pluginFinder.buildMatch())
                .transform((builder, type, classLoader, module) -> {
                    try {
                        List<AbstractClassEnhancePluginDefine> enhancePluginDefines = pluginFinder.find(type);
                        for (AbstractClassEnhancePluginDefine pluginDefine : enhancePluginDefines) {
                            builder = pluginDefine.enhance(builder, type, classLoader);
                        }
                        return builder;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return builder;
                }).installOn(inst);
    }

}
