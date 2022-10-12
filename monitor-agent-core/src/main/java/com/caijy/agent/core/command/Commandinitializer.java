package com.caijy.agent.core.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.caijy.agent.core.constants.AgentConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 初始化命令配置
 *
 * @author liguang
 * @date 2022/10/11 星期二 6:06 下午
 */
public class Commandinitializer {

    /**
     * 默认耗时超过100ms 便认为是高耗时方法
     **/
    public static final int timeCostThreshold = 100;

    /**
     * 默认命令集合
     **/
    public static final LinkedHashMap<String, String> DEFAULT_COMMANDS_KV = Maps.newLinkedHashMap();

    public static final List<String> DEFAULT_COMMANDS = Lists.newArrayList(
        formatCommand(AgentConstant.MONITOR_TIME_COST_THRESHOLD)
    );



    static {
        DEFAULT_COMMANDS_KV.put(formatCommand(AgentConstant.MONITOR_TIME_COST_THRESHOLD), String.valueOf(timeCostThreshold));
    }

    public static String formatCommand(String command){
        return String.format("-D%s",command);
    }
}
