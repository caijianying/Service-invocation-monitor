package com.caijy.agent;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginFinder;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.enhance.Enhancer;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentInterceptor;
import com.caijy.agent.core.plugin.interceptor.enhance.MethodAroundInterceptor;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import com.caijy.agent.tools.interceptor.ThreadConstructorInterceptor;
import com.caijy.agent.tools.interceptor.ThreadInterceptor;
import com.caijy.agent.utils.PluginUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
@Slf4j
public class TraceAgent {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    private static AtomicBoolean objectExtended = new AtomicBoolean(false);

    public static void premain(String agentArgs, Instrumentation inst) {
        Config.init(agentArgs);

        try {
            AgentClassLoader.initDefaultLoader();
        } catch (Exception e) {
            log.error("AgentClassLoader load failed!", e);
        }

        PluginFinder pluginFinder = new PluginFinder(PluginUtil.loadPlugin());
        Junction<TypeDescription> runnableMatch = ElementMatchers.hasSuperType(
            named(Runnable.class.getName()));
        Junction<TypeDescription> callableMatch = ElementMatchers.hasSuperType(
            named(Callable.class.getName()));

        ElementMatcher<? super TypeDescription> buildMatch;
        try {
            buildMatch = pluginFinder.buildMatch();
        } catch (NoSuchMethodError e) {
            log.error(e.getMessage(), e);
            return;
        }

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
            .type(buildMatch)
            .transform((builder, type, classLoader, module) ->
            {
                try {
                    List<AbstractClassEnhancePluginDefine> enhancePluginDefines = pluginFinder.find(type);
                    for (AbstractClassEnhancePluginDefine pluginDefine : enhancePluginDefines) {
                        InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = pluginDefine
                            .getInstanceMethodsInterceptPoints();

                        for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint :
                            instanceMethodsInterceptPoints) {
                            // method
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

                    return builder;
                } catch (Exception e) {
                    log.error("plugin load failed.", e);
                }
                return builder;

            })
            .installOn(inst);

        //new Default().type((runnableMatch.and(nameStartsWith("com.xiaobaicai"))
        //    .or(callableMatch.and(nameStartsWith("com.xiaobaicai")))))
        //    .transform((builder, typeDefinition, classLoader, module) ->{
        //        if (!typeDefinition.isAssignableTo(Enhancer.class)) {
        //            builder = builder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
        //                .implement(Enhancer.class)
        //                .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
        //        }
        //        builder = builder.constructor(isConstructor()).intercept(
        //            SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new ThreadConstructorInterceptor())));
        //        return builder;
        //    })
        //    .transform((builder, typeDefinition, classLoader, module) ->{
        //
        //        builder = builder.method(named("run")
        //            .and(takesArguments(0)).or(named("call").and(takesArguments(0))))
        //            .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new InstrumentInterceptor(new ThreadInterceptor()))));
        //
        //        return builder;
        //    })
        //    .with(new Listener() {
        //        @Override
        //        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        //
        //        }
        //
        //        @Override
        //        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader,
        //            JavaModule module, boolean loaded, DynamicType dynamicType) {
        //            System.out.println("onTransformation，class=" + typeDescription.getName());
        //        }
        //
        //        @Override
        //        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
        //            boolean loaded) {
        //
        //        }
        //
        //        @Override
        //        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded,
        //            Throwable throwable) {
        //            log.error("onError，{}",throwable);
        //        }
        //
        //        @Override
        //        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        //
        //        }
        //    })
        //    .with(RedefinitionStrategy.REDEFINITION)
        //    .installOn(inst);
    }

}
