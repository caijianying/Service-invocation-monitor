package com.caijy.agent.plugin.jdk.thread;

import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.match.ClassMatch;
import com.caijy.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static com.caijy.agent.core.plugin.match.HierarchyMatch.byHierarchyMatch;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author caijy
 * @description
 * @date 2024/1/25 星期四 7:52 下午
 */
public class RunnableInstrumentation extends AbstractClassEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "java.lang.Runnable";

    private static final String INTERCEPTOR_CLASS = "com.caijy.agent.plugin.jdk.thread.JdkThreadMethodInterceptor";

    private static final String CONSTRUCTOR_INTERCEPTOR_CLASS = "com.caijy.agent.plugin.jdk.thread.JdkThreadConstructorInterceptor";


    @Override
    protected ClassMatch enhanceClass() {
        return byHierarchyMatch(ENHANCE_CLASS);
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named("run").and(takesArguments(0));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return ElementMatchers.any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return CONSTRUCTOR_INTERCEPTOR_CLASS;
                    }
                }
        };
    }

    @Override
    public boolean useEnhancedInstance() {
        return true;
    }
}
