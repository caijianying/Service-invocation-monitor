package com.caijy.agent.core.plugin;

import com.caijy.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.caijy.agent.core.plugin.match.ClassMatch;

/**
 * @author liguang
 * @date 2022/12/30 星期五 1:35 下午
 */
public abstract class AbstractClassEnhancePluginDefine {

    protected abstract ClassMatch enhanceClass();

    public abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();
}
