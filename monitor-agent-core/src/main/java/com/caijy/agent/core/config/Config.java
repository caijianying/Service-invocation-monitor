package com.caijy.agent.core.config;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.core.constants.AgentConstant.LogLevel;

/**
 * @author liguang
 * @date 2022/9/21 星期三 9:15 下午
 */
public class Config {

    private static final Map<String, Object> configMap = new HashMap<>();

    static {
        configMap.put(AgentConstant.MONITOR_AGENT_PATH, System.getProperty(AgentConstant.MONITOR_AGENT_PATH));
        configMap.put(AgentConstant.MONITOR_LOG_LEVEL,
            System.getProperty(AgentConstant.MONITOR_LOG_LEVEL, LogLevel.INFO));
    }

    public static Object get(String configName) {
        return configMap.get(configName);
    }

    public static void init(String agentArgs) {
        // 初始化配置
        configMap.put(AgentConstant.MONITOR_AGENT_PATH, System.getProperty(AgentConstant.MONITOR_AGENT_PATH));
        configMap.put(AgentConstant.MONITOR_LOG_LEVEL,
            System.getProperty(AgentConstant.MONITOR_AGENT_PATH, LogLevel.INFO));

        // 设置自定义包名或mainClass包名
        if (StrUtil.isNotBlank(agentArgs) && agentArgs.indexOf("=") == -1) {
            configMap.put(AgentConstant.MONITOR_PACKAGE, agentArgs);
        } else if (StrUtil.isBlank(agentArgs)) {
            configMap.put(AgentConstant.MONITOR_PACKAGE, System.getProperty(AgentConstant.MONITOR_PACKAGE));
        }

        // 设置高耗时阀值 ms
        configMap.put(AgentConstant.MONITOR_TIME_COST_THRESHOLD,
            System.getProperty(AgentConstant.MONITOR_TIME_COST_THRESHOLD, "100"));

        // 采样率
        configMap.put(AgentConstant.MONITOR_SAMPLE_RATE,
            System.getProperty(AgentConstant.MONITOR_SAMPLE_RATE, "10000"));

        // 监控的项目编号
        String projectCode = System.getProperty(AgentConstant.MONITOR_PROJECT_CODE);
        configMap.put(AgentConstant.MONITOR_PROJECT_CODE, projectCode);
    }
}
