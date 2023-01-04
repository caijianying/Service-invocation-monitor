package com.caijy.agent.plugin.spring.annotation;

import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.match.ClassAnnotationMatch;
import com.caijy.agent.core.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author liguang
 * @date 2022/12/30 星期五 11:43 上午
 */
public abstract class AbstractSpringAnnotationInstrumentation extends AbstractClassEnhancePluginDefine {

    public static final String INTERCEPTOR_CLASS
        = "com.caijy.agent.plugin.spring.annotation.SpringAnnotationInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationMatch.byClassAnnotationMatch(getEnhanceAnnotations());
    }

    protected abstract String[] getEnhanceAnnotations();

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return null;
                }

                @Override
                public String getMethodsInterceptor() {
                    return INTERCEPTOR_CLASS;
                }
            }
        };
    }
}
