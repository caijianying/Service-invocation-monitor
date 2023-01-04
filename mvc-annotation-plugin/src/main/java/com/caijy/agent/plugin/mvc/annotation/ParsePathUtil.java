package com.caijy.agent.plugin.mvc.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Tools for parsing path from annotation
 */
public class ParsePathUtil {

    public static String recursiveParseMethodAnnotation(Method method, Function<Method, String> parseFunc) {
        String result = parseFunc.apply(method);
        if (Objects.isNull(result)) {
            Class<?> declaringClass = method.getDeclaringClass();
            result = recursiveMatches(declaringClass, method.getName(), method.getParameters(), parseFunc);
        }
        return Optional.ofNullable(result).orElse("");
    }

    private static String recursiveMatches(Class clazz, String methodName, Parameter[] parameters,
        Function<Method, String> parseFunc) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> implInterface : interfaces) {
            String path = recursiveMatches(implInterface, methodName, parameters, parseFunc);
            if (Objects.nonNull(path)) {
                return path;
            }
            Method[] declaredMethods = implInterface.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (Objects.equals(declaredMethod.getName(), methodName) && parameterEquals(parameters, declaredMethod.getParameters())) {
                    return parseFunc.apply(declaredMethod);
                }
            }
        }
        return null;
    }

    private static boolean parameterEquals(Parameter[] p1, Parameter[] p2) {
        if (p1.length != p2.length) {
            return false;
        }
        for (int i = 0; i < p1.length; i++) {
            if (!Objects.equals(p1[i].getType().getName(), p2[i].getType().getName())) {
                return false;
            }
        }
        return true;
    }
}
