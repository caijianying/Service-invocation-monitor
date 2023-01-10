package com.caijy.test.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

import com.caijy.test.agent.enhancer.Enhancer;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.description.modifier.ModifierContributor.ForMethod;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;

/**
 * @author liguang
 * @date 2023/1/9 星期一 11:39 上午
 */
@Slf4j
public class TestTraceAgent {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    private static AtomicBoolean objectExtended = new AtomicBoolean(false);

    public static void premain(String agentArgs, Instrumentation inst) {
        log.debug("xxxx!XXX!{}","Good!");
        log.info("xxxx!XXX!{}","Good!");
        log.error("xxxx!XXX!{}","Good!");

        //Unloaded<Object> dynamicType = new ByteBuddy()
        //    .subclass(Object.class)
        //    .name("com.xiaobaicai.Logger")
        //    .defineMethod("log", void.class, Modifier.PUBLIC + Modifier.STATIC)
        //    .withParameter(String.class)
        //    .intercept(MethodDelegation.to(LogInterceptor.class))
        //    .make();
        //
        //// 加载类
        //Class<?> clazz = dynamicType.load(TestTraceAgent.class.getClassLoader())
        //    .getLoaded();
        //
        //// 反射调用
        //try {
        //    clazz.getMethod("log", String.class).invoke(null, "com.ywwl.foundation.teamwork.Application");
        //} catch (IllegalAccessException e) {
        //    e.printStackTrace();
        //} catch (InvocationTargetException e) {
        //    e.printStackTrace();
        //} catch (NoSuchMethodException e) {
        //    e.printStackTrace();
        //}

        //final ClassLoader[] classLoaders = new ClassLoader[1];
        //final TypeDescription[] typeDescriptions = new TypeDescription[1];
        //new Default().type(ElementMatchers.named("org.slf4j.LoggerFactory"))
        //    .transform((builder, typeDefinitionX, classLoaderX, module) -> {
        //        builder = builder.defineMethod("log", void.class, Modifier.PUBLIC + Modifier.STATIC)
        //            .withParameter(String.class)
        //            .intercept(MethodDelegation.to(LogInterceptor.class));
        //        classLoaders[0] = classLoaderX;
        //        typeDescriptions[0] = typeDefinitionX;
        //
        //        try {
        //            Class<?> loadClass = classLoaders[0].loadClass(typeDescriptions[0].getName());
        //            Logger getLogger = (Logger)loadClass.getDeclaredMethod("getLogger", String.class).invoke(null,
        //                "com.ywwl.foundation.teamwork.Application");
        //            getLogger.info("TestTraceAgent >>> :{}", "getLogger");
        //        } catch (ClassNotFoundException | NoSuchMethodException e) {
        //            e.printStackTrace();
        //        } catch (InvocationTargetException e) {
        //            e.printStackTrace();
        //        } catch (IllegalAccessException e) {
        //            e.printStackTrace();
        //        }
        //        return builder;
        //    }).with(RedefinitionStrategy.RETRANSFORMATION)
        //    .installOn(inst);

        //try {
        //    Class<?> loadClass = classLoaders[0].loadClass(typeDescriptions[0].getName());
        //    Logger getLogger = (Logger)loadClass.getDeclaredMethod("getLogger", String.class).invoke(null,
        //        "com.ywwl.foundation.teamwork.Application");
        //    getLogger.info("TestTraceAgent >>> :{}", "getLogger");
        //} catch (ClassNotFoundException | NoSuchMethodException e) {
        //    e.printStackTrace();
        //} catch (InvocationTargetException e) {
        //    e.printStackTrace();
        //} catch (IllegalAccessException e) {
        //    e.printStackTrace();
        //}

        //new Default().type(ElementMatchers.named("org.slf4j.LoggerFactory"))
        //    .transform((builder, typeDefinition, classLoader, module) -> {
        //        try {
        //
        //            builder.defineMethod("getLogger2",TypeDescription.OBJECT, Modifier.PUBLIC + Modifier.STATIC)
        //                .withParameter(String.class)
        //                .intercept(MethodDelegation.to(LogInterceptor.class))
        //                .make();
        //
        //
        //            Class<?> loadClass = classLoader.loadClass(typeDefinition.getName());
        //            Logger getLogger = (Logger)loadClass.getDeclaredMethod("getLogger", String.class).invoke(null,
        //            "com.ywwl.foundation.teamwork.Application");
        //            getLogger.info("TestTraceAgent >>> :{}", "getLogger");
        //        } catch (ClassNotFoundException | NoSuchMethodException e) {
        //            e.printStackTrace();
        //        } catch (InvocationTargetException e) {
        //            e.printStackTrace();
        //        } catch (IllegalAccessException e) {
        //            e.printStackTrace();
        //        }
        //
        //        //if (!objectExtended.get()) {
        //        //    // 是否实现了这个接口，若没有 则增强
        //        //    if (!typeDefinition.isAssignableTo(Enhancer.class)) {
        //        //        builder = builder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
        //        //            .implement(Enhancer.class)
        //        //            .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
        //        //        objectExtended.compareAndSet(false, true);
        //        //    }
        //        //}
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
        //
        //        }
        //
        //        @Override
        //        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        //
        //        }
        //    })
        //    .with(RedefinitionStrategy.RETRANSFORMATION)
        //    .installOn(inst);

        //new Default().type(ElementMatchers.nameStartsWith("org.slf4j"))
        //    .transform((builder, typeDefinition, classLoader, module) -> {
        //        if ("org.slf4j.LoggerFactory".equals(typeDefinition.getName())) {
        //            try {
        //                Class<?> loadClass = classLoader.loadClass(typeDefinition.getName());
        //                Logger getLogger = (Logger)loadClass.getDeclaredMethod("getLogger", String.class).invoke
        //                (null,"com.ywwl.foundation.teamwork.Application");
        //                getLogger.info("TestTraceAgent >>> :{}", "getLogger");
        //            } catch (ClassNotFoundException | NoSuchMethodException e) {
        //                e.printStackTrace();
        //            } catch (InvocationTargetException e) {
        //                e.printStackTrace();
        //            } catch (IllegalAccessException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //
        //        //if (!objectExtended.get()) {
        //        //    // 是否实现了这个接口，若没有 则增强
        //        //    if (!typeDefinition.isAssignableTo(Enhancer.class)) {
        //        //        builder = builder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
        //        //            .implement(Enhancer.class)
        //        //            .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
        //        //        objectExtended.compareAndSet(false, true);
        //        //    }
        //        //}
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
        //
        //        }
        //
        //        @Override
        //        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        //
        //        }
        //    })
        //    .with(RedefinitionStrategy.RETRANSFORMATION)
        //    .installOn(inst);

        //new Default().type(ElementMatchers.nameStartsWith("org.slf4j"))
        //    .transform((builder, typeDefinition, classLoader, module) -> {
        //        if (!objectExtended.get()) {
        //            // 是否实现了这个接口，若没有 则增强
        //            if (!typeDefinition.isAssignableTo(Enhancer.class)) {
        //                builder = builder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
        //                    .implement(Enhancer.class)
        //                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
        //                objectExtended.compareAndSet(false, true);
        //            }
        //        }
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
        //
        //        }
        //
        //        @Override
        //        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        //
        //        }
        //    })
        //    .with(RedefinitionStrategy.RETRANSFORMATION)
        //    .installOn(inst);
    }
}
