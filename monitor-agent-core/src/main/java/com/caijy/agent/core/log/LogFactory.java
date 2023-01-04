package com.caijy.agent.core.log;

import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.constants.AgentConstant;

/**
 * @author liguang
 * @date 2022/12/30 星期五 10:25 上午
 */
public class LogFactory {

    public static Logger getLogger(Class clazz){
        return new Logger(Config.get(AgentConstant.MONITOR_LOG_LEVEL).toString(),clazz.getName());
    }
}
