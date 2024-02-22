package com.caijy.agent;

import com.caijy.agent.core.boot.ServiceManager;
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
                                .or(nameStartsWith("org.apache.dubbo.config"))
                                .or(nameStartsWith("org.apache.catalina"))
                                .or(nameStartsWith("org.apache.tomcat"))
                                .or(nameStartsWith("org.apache.zookeeper."))
                                .or(nameStartsWith("org.apache.curator."))
                                .or(nameStartsWith("org.apache.dubbo.metadata"))
                                .or(named("org.apache.dubbo.metadata.MetadataService"))
                                .or(nameStartsWith("org.apache.dubbo.remoting"))
                                .or(nameStartsWith("org.apache.dubbo.registry"))
                                .or(nameStartsWith("org.apache.dubbo.common."))
                                .or(nameStartsWith("org.springframework.boot."))
                                .or(nameStartsWith("com.intellij.rt.execution"))
                                .or(nameStartsWith("io.netty."))
                                .or(not(isPublic()))
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

        try {
            ServiceManager.INSTANCE.boot();
        } catch (Throwable ex) {
            log.error("Service-invocation-monitor agent boot failure.", ex);
        }

        Runtime.getRuntime()
                .addShutdownHook(new Thread(ServiceManager.INSTANCE::shutdown, "Service-invocation-monitor service shutdown thread ..."));
    }

}
