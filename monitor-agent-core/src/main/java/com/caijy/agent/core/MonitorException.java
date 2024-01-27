package com.caijy.agent.core;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caijy
 * @description
 * @date 2023/11/8 星期三 9:46 上午
 */
@Slf4j
public class MonitorException extends RuntimeException {

    public static void error(Class currentClazz, Throwable ex) {
        log.error(
                "Service Invocation Monitor | An Error occurred! from class:{},"
                        + "reason:{}",
                currentClazz.getClass().getSimpleName(), ex.getMessage(), ex);
    }
}
