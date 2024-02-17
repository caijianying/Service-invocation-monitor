package com.caijy.agent.core.boot;

/**
 * @author caijy
 * @description
 * @date 2024/2/17 星期六 4:51 下午
 */
public interface BootService {

    void boot() throws Throwable;

    void shutdown() throws Throwable;

}
