package com.caijy.agent.test;

import com.caijy.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;

/**
 * @author caijy
 * @description
 * @date 2024/2/17 星期六 3:25 下午
 */
@Slf4j
public class SimpleAgent {
    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("SimpleAgent....premain");
        new AgentBuilder.Default()
                .type(ElementMatchers.named("com.xiaobaicai.domain.dto.UserDto").or(ElementMatchers.named("com.xiaobaicai.api.impl.UserServiceImpl")))
                .transform((builder, type, classLoader, module) ->
                {
                    builder = builder.defineField(
                            CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                            .implement(EnhancedInstance.class)
                            .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                    return builder;
                })
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .installOn(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        System.out.println("SimpleAgent....agentmain");
        // 添加到启动类加载器，为了让所有classloader能加载到agent包中的类
        String clazzName = SimpleAgent.class.getName().replace(".", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(clazzName);
        if (resource.getProtocol().equals("jar")) {
            int index = resource.getPath().indexOf("!/");
            if (index > -1) {
                String jarFile = resource.getPath().substring("file:".length(), index);
                inst.appendToBootstrapClassLoaderSearch(new JarFile(new File(jarFile)));
            }
        }

        DemoClassFileTransformer transformer = new DemoClassFileTransformer();
        inst.addTransformer(transformer, true);

        Map<String, ClassLoader> classLoaderMap = new HashMap<>();

        for (Class loadedClass : inst.getAllLoadedClasses()) {
            classLoaderMap.put(loadedClass.getName(), loadedClass.getClassLoader());
        }

        String className = "com.xiaobaicai.api.impl.UserServiceImpl";
        ClassLoader loader = classLoaderMap.get(className);
        Class<?> loadClass = loader.loadClass(className);
        inst.retransformClasses(loadClass);

    }

    public static class DemoClassFileTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (classfileBuffer != null) {
                String dir = "/Users/jianyingcai/IdeaProjects/practice/Service-Invocation-Monitor-Demo/lib/dump";
                File tmpFile = new File(dir + File.separator + className.replaceAll("/", ".") + ".class");
                try {
                    if (!tmpFile.exists()) {
                        String parent = tmpFile.getParent();
                        File file = new File(parent);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        tmpFile.createNewFile();
                    }
                    // 写入文件
                    FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                    fileOutputStream.write(classfileBuffer);
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return classfileBuffer;
        }
    }
}
