//package com.caijy.agent;
//
//import java.lang.instrument.Instrumentation;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
//import com.caijy.agent.core.config.Config;
//import com.caijy.agent.core.plugin.interceptor.enhance.InstrumentInterceptor;
//import net.bytebuddy.agent.builder.AgentBuilder;
//import net.bytebuddy.build.HashCodeAndEqualsPlugin;
//import net.bytebuddy.description.annotation.AnnotationDescription;
//import net.bytebuddy.description.annotation.AnnotationList;
//import net.bytebuddy.description.annotation.AnnotationSource;
//import net.bytebuddy.description.method.MethodDescription;
//import net.bytebuddy.description.method.MethodDescription.InGenericShape;
//import net.bytebuddy.description.method.MethodList;
//import net.bytebuddy.description.method.ParameterList;
//import net.bytebuddy.description.type.TypeDefinition;
//import net.bytebuddy.description.type.TypeDescription;
//import net.bytebuddy.description.type.TypeList;
//import net.bytebuddy.implementation.MethodDelegation;
//import net.bytebuddy.matcher.CollectionItemMatcher;
//import net.bytebuddy.matcher.ElementMatcher;
//import net.bytebuddy.matcher.ElementMatcher.Junction;
//import net.bytebuddy.matcher.ElementMatchers;
//
//import static net.bytebuddy.matcher.ElementMatchers.annotationType;
//import static net.bytebuddy.matcher.ElementMatchers.isInterface;
//import static net.bytebuddy.matcher.ElementMatchers.isPublic;
//import static net.bytebuddy.matcher.ElementMatchers.isStatic;
//import static net.bytebuddy.matcher.ElementMatchers.nameContains;
//import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
//import static net.bytebuddy.matcher.ElementMatchers.named;
//import static net.bytebuddy.matcher.ElementMatchers.not;
//
///**
// * @author liguang
// * @date 2022/7/5 星期二 8:49 下午
// */
//public class TraceAgent {
//
//    public static void premain(String agentArgs, Instrumentation inst) {
//        Config.init(agentArgs);
//        Junction<? super TypeDescription> judge = ElementMatchers.isAnnotatedWith(
//            named("org.springframework.stereotype.Service")
//                .or(named("org.springframework.web.bind.annotation.RestController"))
//            //.or(named("org.springframework.context.annotation.Bean"))
//            //.or(named("org.springframework.stereotype.Component"))
//            //.or(named("org.springframework.stereotype.Repository"))
//        );
//        judge = judge.and(not(isInterface()));
//        Junction<? super TypeDescription> junction = judge.or(
//            ElementMatchers.named("com.alibaba.dubbo.monitor.support.MonitorFilter")
//                .or(named("org.apache.dubbo.monitor.support.MonitorFilter")));
//
//        new AgentBuilder.Default()
//            .ignore(
//                nameStartsWith("net.bytebuddy.")
//                    .or(nameStartsWith("org.slf4j."))
//                    .or(nameStartsWith("org.groovy."))
//                    .or(nameContains("javassist"))
//                    .or(nameContains(".asm."))
//                    .or(nameContains(".reflectasm."))
//                    .or(nameStartsWith("sun.reflect"))
//                    .or(ElementMatchers.isSynthetic())
//            )
//            .type(junction)
//            //.transform((builder, type, classLoader, module) ->
//            //    builder.method(ElementMatchers.not(isStatic()).and(isPublic()).and(ElementMatchers.any()))
//            //        .intercept(MethodDelegation.to(new InstrumentInterceptor(
//            //            Lists.newArrayList(new DubboInterceptor(), new SpringInterceptor())))))
//            .transform((builder, type, classLoader, module) ->
//            {
//                //Junction<AnnotationSource> sourceJunction = byMethodInheritanceAnnotationMatcher(
//                //    named("org.springframework.web.bind.annotation.RequestMapping"))
//                //    .or(byMethodInheritanceAnnotationMatcher(
//                //        named("org.springframework.web.bind.annotation.GetMapping")))
//                //    .or(byMethodInheritanceAnnotationMatcher(
//                //        named("org.springframework.web.bind.annotation.PostMapping")))
//                //    .or(byMethodInheritanceAnnotationMatcher(
//                //        named("org.springframework.web.bind.annotation.PutMapping")))
//                //    .or(byMethodInheritanceAnnotationMatcher(
//                //        named("org.springframework.web.bind.annotation.DeleteMapping")))
//                //    .or(byMethodInheritanceAnnotationMatcher(
//                //        named("org.springframework.web.bind.annotation.PatchMapping")));
//                builder = builder.method(ElementMatchers.not(isStatic()).and(isPublic())
//                    .and(ElementMatchers.named("invoke")))
//                    .intercept(MethodDelegation.to(new InstrumentInterceptor(new DubboInterceptor())));
//                boolean match = isMatch(type);
//                if (match) {
//                    builder = builder.method(ElementMatchers.not(isStatic()).and(isPublic()).and(ElementMatchers.any()))
//                        .intercept(MethodDelegation.to(new InstrumentInterceptor(new SpringInterceptor())));
//                }
//                return builder;
//
//            })
//            .installOn(inst);
//    }
//
//    public static boolean isMatch(TypeDescription typeDescription) {
//        List<String> annotations = Arrays.asList("org.springframework.stereotype.Service",
//            "org.springframework.web.bind.annotation.RestController","org.springframework.stereotype.Component");
//        List<String> annotationList = new ArrayList<String>(annotations);
//        AnnotationList declaredAnnotations = typeDescription.getDeclaredAnnotations();
//        for (AnnotationDescription annotation : declaredAnnotations) {
//            annotationList.remove(annotation.getAnnotationType().getActualName());
//        }
//        return annotationList.size() != annotations.size();
//    }
//
//    public static <T extends AnnotationSource> ElementMatcher.Junction<T> byMethodInheritanceAnnotationMatcher(
//        ElementMatcher<? super TypeDescription> matcher) {
//        return new MethodInheritanceAnnotationMatcher(new CollectionItemMatcher<>(annotationType(matcher)));
//    }
//
//    public static void agentmain(String args, Instrumentation inst) {
//
//    }
//
//    @HashCodeAndEqualsPlugin.Enhance
//    static class MethodInheritanceAnnotationMatcher<T extends MethodDescription>
//        extends ElementMatcher.Junction.AbstractBase<T> {
//        private final ElementMatcher<? super AnnotationList> matcher;
//
//        public MethodInheritanceAnnotationMatcher(ElementMatcher<? super AnnotationList> matcher) {
//            this.matcher = matcher;
//        }
//
//        @Override
//        public boolean matches(T target) {
//            if (matcher.matches(target.getDeclaredAnnotations())) {
//                return true;
//            }
//            String name = target.getName();
//            ParameterList<?> parameters = target.getParameters();
//
//            TypeDefinition declaringType = target.getDeclaringType();
//            return recursiveMatches(declaringType, name, parameters);
//        }
//
//        private boolean recursiveMatches(TypeDefinition typeDefinition, String methodName,
//            ParameterList<?> parameters) {
//            TypeList.Generic interfaces = typeDefinition.getInterfaces();
//            for (TypeDescription.Generic implInterface : interfaces) {
//                if (recursiveMatches(implInterface, methodName, parameters)) {
//                    return true;
//                }
//                MethodList<InGenericShape> declaredMethods = implInterface.getDeclaredMethods();
//                for (MethodDescription declaredMethod : declaredMethods) {
//                    if (Objects.equals(declaredMethod.getName(), methodName) && parameterEquals(parameters,
//                        declaredMethod.getParameters())) {
//                        return matcher.matches(declaredMethod.getDeclaredAnnotations());
//                    }
//                }
//            }
//            return false;
//        }
//
//        private boolean parameterEquals(ParameterList<?> source, ParameterList<?> impl) {
//            if (source.size() != impl.size()) {
//                return false;
//            }
//            for (int i = 0; i < source.size(); i++) {
//                if (!Objects.equals(source.get(i).getType(), impl.get(i).getType())) {
//                    return false;
//                }
//            }
//            return true;
//        }
//    }
//
//    //private static class Transformer implements AgentBuilder.Transformer {
//    //
//    //    @Override
//    //    public DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder,
//    //        final TypeDescription typeDescription,
//    //        final ClassLoader classLoader,
//    //        final JavaModule module) {
//    //
//    //        builder.method(
//    //            ElementMatchers.not(isStatic()).and(ElementMatchers.named("invoke").or(named("makeWrapper"))))
//    //            .intercept(MethodDelegation.to(new InstrumentInterceptor(new DubboInterceptor())));
//    //        return builder;
//    //    }
//    //}
//}
