package com.caijy.agent.core.plugin;

import com.caijy.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.OverrideCallable;
import com.caijy.agent.core.plugin.interceptor.enhance.*;
import com.caijy.agent.core.plugin.loader.InterceptorInstanceLoader;
import com.caijy.agent.core.plugin.match.ClassMatch;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;

/**
 * @author liguang
 * @date 2022/12/30 星期五 1:35 下午
 */
@Slf4j
public abstract class AbstractClassEnhancePluginDefine {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    protected abstract ClassMatch enhanceClass();

    public abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();

    public abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    public abstract boolean useEnhancedInstance();

    public DynamicType.Builder<?> enhance(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
        try {
            if (classLoader != null){
                System.out.println("enhance>>>: "+classLoader.getClass().getName());
            }
            // 先进行字段属性织入
            if (useEnhancedInstance()) {
                if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
                    builder = builder.defineField(
                            CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                            .implement(EnhancedInstance.class)
                            .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                }
            }

            // 增强构造方法
            ConstructorInterceptPoint[] constructorsInterceptPoints = getConstructorsInterceptPoints();
            for (ConstructorInterceptPoint constructorsInterceptPoint : constructorsInterceptPoints) {
                Object newInstance = InterceptorInstanceLoader.load(constructorsInterceptPoint.getConstructorInterceptor(),classLoader);
                builder = builder
                        .constructor(constructorsInterceptPoint.getConstructorMatcher())
                        .intercept(SuperMethodCall.INSTANCE
                                .andThen(MethodDelegation.to(new ConstructorInter((InstanceConstructorInterceptor) newInstance))));
            }

            // 增强实例方法
            InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = this.getInstanceMethodsInterceptPoints();
            for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint : instanceMethodsInterceptPoints) {
                ElementMatcher<MethodDescription> methodsMatcher = instanceMethodsInterceptPoint.getMethodsMatcher();
                if (methodsMatcher == null) {
                    methodsMatcher = any();
                }
                Object newInstance = InterceptorInstanceLoader.load(instanceMethodsInterceptPoint.getMethodsInterceptor(),classLoader);
                ElementMatcher.Junction<MethodDescription> junction = not((ElementMatcher) isStatic()).and(isPublic());
                if (instanceMethodsInterceptPoint.isOverrideArgs()) {
                    builder = builder
                            .method(junction.and(methodsMatcher))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .withBinders(Morph.Binder.install(OverrideCallable.class)).to(new InstrumentMethodInterceptor((MethodAroundInterceptorV1) newInstance)));

                } else {
                    builder = builder.method(junction.and(methodsMatcher)).intercept(MethodDelegation.to(new InstrumentMethodInterceptor((MethodAroundInterceptorV1) newInstance)));
                }
            }
        } catch (Throwable ex) {
            log.error("enhance class occurs error ! error msg is {}", ex.getMessage(), ex);
        }
        return builder;
    }
}
