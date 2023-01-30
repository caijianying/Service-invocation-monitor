package com.caijy.agent.plugin.dubbo;

import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.match.ClassMatch;
import com.caijy.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author liguang
 * @date 2022/12/30 星期五 3:09 下午
 */
public class AlibabaDubboInstrumentation extends AbstractClassEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "com.alibaba.dubbo.monitor.support.MonitorFilter";

    private static final String INTERCEPTOR_CLASS = "com.caijy.agent.plugin.dubbo.DubboInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return ElementMatchers.named("invoke");
                }

                @Override
                public String getMethodsInterceptor() {
                    return INTERCEPTOR_CLASS;
                }
            }
        };
    }
}
