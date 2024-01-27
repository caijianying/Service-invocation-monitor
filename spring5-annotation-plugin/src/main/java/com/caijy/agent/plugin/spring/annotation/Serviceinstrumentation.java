package com.caijy.agent.plugin.spring.annotation;

import com.caijy.agent.core.plugin.interceptor.ConstructorInterceptPoint;

/**
 * @author liguang
 * @date 2022/12/30 星期五 3:41 下午
 */
public class Serviceinstrumentation extends AbstractSpringAnnotationInstrumentation{


    public static final String ENHANCE_ANNOTATION = "org.springframework.stereotype.Service";

    @Override
    protected String[] getEnhanceAnnotations() {
        return new String[]{ENHANCE_ANNOTATION};
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }
}
