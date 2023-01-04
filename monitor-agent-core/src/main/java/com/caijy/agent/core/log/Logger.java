package com.caijy.agent.core.log;

import com.caijy.agent.core.constants.AgentConstant.LogLevel;

/**
 * @author liguang
 * @date 2022/12/30 星期五 10:25 上午
 */
public class Logger {

    private String level;

    private String className;

    public Logger() {
    }

    public Logger(String level, String className) {
        this.level = level;
        this.className = className;
    }

    public void debug(String format, Object... args) {
        if (LogLevel.DEBUG.equals(level)) {
            System.out.println(String.format("[%s] DEBUG: " + format, this.className, args));
        }
    }
}
