package com.caijy.plugin.context;

import java.util.Map;

import com.caijy.plugin.constants.AgentConstant;
import com.google.common.collect.Maps;

/**
 * @author liguang
 * @date 2022/9/21 星期三 9:15 下午
 */
public class Config {

    private static final Map<String, Object> configMap = Maps.newHashMap();

    public static Object getConfig(String configName) {
        return configMap.get(configName);
    }

    public static void initConfig(String agentArgs) {
        // 初始化配置
        configMap.put(AgentConstant.MONITOR_TIME_COST_THRESHOLD, 100);
        if (agentArgs.indexOf("=") == -1) {
            configMap.put(AgentConstant.MONITOR_PACKAGE, agentArgs);
        } else {
            String[] splitArgs = agentArgs.split("=");

            for (int i = 0, len = splitArgs.length; i < len && i + 1 < len; i = i + 2) {
                configMap.put(splitArgs[i], splitArgs[i + 1]);
            }
        }
    }
}
