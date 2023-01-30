package com.caijy.agent.plugin.mvc.annotation;

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
public class RestControllerInstrumentation extends AbstractClassEnhancePluginDefine {

    public static final String ENHANCE_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

    public static final String INTERCEPTOR_CLASS = "com.caijy.agent.plugin.mvc.annotation.MvcAnnotationInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationMatch.byClassAnnotationMatch(new String[] {ENHANCE_ANNOTATION});
    }

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
