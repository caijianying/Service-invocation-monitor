package com.caijy.agent.plugin.spring.annotation;

/**
 * @author liguang
 * @date 2022/12/30 星期五 11:43 上午
 */
public class RestControllerInstrumentation extends AbstractSpringAnnotationInstrumentation {

    public static final String ENHANCE_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

    @Override
    protected String[] getEnhanceAnnotations() {
        return new String[] {ENHANCE_ANNOTATION};
    }
}
