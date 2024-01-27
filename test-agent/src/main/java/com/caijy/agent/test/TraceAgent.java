package com.caijy.agent.test;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginFinder;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import com.caijy.agent.test.enhancer.EnhancedInstance;
import com.caijy.agent.test.models.InstrumentConstructorInterceptor;
import com.caijy.agent.test.models.InstrumentMethodInterceptor;
import com.caijy.agent.test.models.OverrideCallable;
import com.caijy.agent.test.models.interceptors.MvcGetMappingInterceptor;
import com.caijy.agent.test.models.interceptors.RunnableOrCallableConstructorInterceptor;
import com.caijy.agent.test.models.interceptors.RunnableOrCallableIntercepter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.Callable;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
@Slf4j
public class TraceAgent {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    public static void premain(String agentArgs, Instrumentation inst) {

        Config.init(agentArgs);
        try {
            AgentClassLoader.initDefaultLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String className = "com.xiaobaicai.thread.model.TestRunnable";
        new AgentBuilder.Default()
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
                .type(nameStartsWith("com.xiaobaicai").and(named(className)))
                .transform((builder, typeDescription, classLoader, module) -> {
                    System.out.println(typeDescription.getName() + "..." + typeDescription.isAssignableTo(EnhancedInstance.class));
                    if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
                        builder = builder.defineField(
                                CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                                .implement(EnhancedInstance.class)
                                .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                    }
                    builder = builder
                            .constructor(any())
                            .intercept(SuperMethodCall.INSTANCE
                                    .andThen(MethodDelegation.to(new InstrumentConstructorInterceptor(new RunnableOrCallableConstructorInterceptor()))))
                            .method(isAnnotatedWith(named("org.springframework.web.bind.annotation.GetMapping")))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .to(new InstrumentMethodInterceptor(new MvcGetMappingInterceptor())))
                            .method(named("call").and(takesArguments(0)).or(named("run").and(takesArguments(0))))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .withBinders(Morph.Binder.install(OverrideCallable.class))
                                    .to(new InstrumentMethodInterceptor(new RunnableOrCallableIntercepter())));
                    return builder;
                })
                .installOn(inst);

    }

    private static AgentBuilder.Listener.Adapter getListener(String interceptorClassName) {
        return new AgentBuilder.Listener.Adapter() {
            @Override
            public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                String msg = String.format("Transform Error: interceptorClassName: %s, typeName: %s, classLoader: %s, module: %s, loaded: %s", interceptorClassName, typeName, classLoader, module, loaded);
                System.err.println(msg);
                throwable.printStackTrace();
            }
        };
    }
}
