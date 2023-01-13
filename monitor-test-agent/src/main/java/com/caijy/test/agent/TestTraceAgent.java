package com.caijy.test.agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import com.caijy.agent.core.plugin.interceptor.enhance.Enhancer;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:49 下午
 */
@Slf4j
public class TestTraceAgent {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    private static AtomicBoolean objectExtended = new AtomicBoolean(false);

    public static void premain(String agentArgs, Instrumentation inst) {

        Junction<TypeDescription> runnableMatch = ElementMatchers.hasSuperType(
            named(Runnable.class.getName()));
        Junction<TypeDescription> callableMatch = ElementMatchers.hasSuperType(
            named(Callable.class.getName()));
        new Default().type((runnableMatch.and(nameStartsWith("com.xiaobaicai"))
            .or(callableMatch.and(nameStartsWith("com.xiaobaicai")))))
            .transform((builder, typeDefinition, classLoader, module) ->{
                if (!typeDefinition.isAssignableTo(Enhancer.class)) {
                    builder = builder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                        .implement(Enhancer.class)
                        .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                }
                builder = builder.constructor(isConstructor()).intercept(
                    SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new DemoConstructorInterceptor())));
                return builder;
            })
            .transform((builder, typeDefinition, classLoader, module) ->{

                builder = builder.method(named("run")
                    .and(takesArguments(0)).or(named("call").and(takesArguments(0))))
                    .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new DemoInterceptor())));

                return builder;
            })
            .with(new Listener() {
                @Override
                public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                }

                @Override
                public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader,
                    JavaModule module, boolean loaded, DynamicType dynamicType) {
                    System.out.println("onTransformation，class=" + typeDescription.getName());
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                    boolean loaded) {

                }

                @Override
                public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded,
                    Throwable throwable) {
                    log.error("onError，{}",throwable);
                }

                @Override
                public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                }
            })
            .with(RedefinitionStrategy.REDEFINITION)
            .installOn(inst);
    }

}
