package com.caijy.test.agent.enhancer;

/**
 * @author liguang
 * @date 2023/1/9 星期一 11:44 上午
 */
public interface Enhancer {

    Object getDynamicField();

    void setDynamicField(Object value);
}
