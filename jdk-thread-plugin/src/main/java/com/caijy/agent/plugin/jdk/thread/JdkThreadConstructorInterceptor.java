package com.caijy.agent.plugin.jdk.thread;

import com.caijy.agent.core.plugin.context.ContextManager;
import com.caijy.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.caijy.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caijy
 * @description
 * @date 2024/1/25 星期四 7:59 下午
 */
@Slf4j
public class JdkThreadConstructorInterceptor implements InstanceConstructorInterceptor {
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        if (ContextManager.isActive()){
//            log.debug("JdkThreadConstructorInterceptor>>: {}",objInst.getClass().getName());
            objInst.setDynamicField(ContextManager.capture());
        }
    }
}
