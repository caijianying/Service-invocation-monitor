package com.caijy.agent.context;

import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.constants.AgentConstant;
import com.google.common.collect.Maps;

/**
 * @author liguang
 * @date 2022/9/21 星期三 9:15 下午
 */
public class Config {

    private static final Map<String, Object> configMap = Maps.newHashMap();

    public static Object get(String configName) {
        return configMap.get(configName);
    }

    public static void init(String agentArgs) {
        // 初始化配置
        configMap.put(AgentConstant.MONITOR_TIME_COST_THRESHOLD, 100);

        // 设置自定义包名或mainClass包名
        if (StrUtil.isNotBlank(agentArgs) && agentArgs.indexOf("=") == -1) {
            configMap.put(AgentConstant.MONITOR_PACKAGE, agentArgs);
        }else if (StrUtil.isBlank(agentArgs)){
            configMap.put(AgentConstant.MONITOR_PACKAGE, System.getProperty(AgentConstant.MONITOR_PACKAGE));
        }

        // 设置高耗时阀值 ms
        String threshold = System.getProperty(AgentConstant.MONITOR_TIME_COST_THRESHOLD);
        if (StrUtil.isNotBlank(threshold)) {
            configMap.put(AgentConstant.MONITOR_TIME_COST_THRESHOLD, threshold);
        }

    }
}
