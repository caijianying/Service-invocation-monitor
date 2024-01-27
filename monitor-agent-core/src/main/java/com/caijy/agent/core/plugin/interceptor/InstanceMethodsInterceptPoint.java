package com.caijy.agent.core.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author liguang
 * @date 2022/12/30 星期五 3:14 下午
 */
public interface InstanceMethodsInterceptPoint {

    ElementMatcher<MethodDescription> getMethodsMatcher();

    String getMethodsInterceptor();

    boolean isOverrideArgs();
}
