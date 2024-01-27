package com.caijy.agent.test.models;

import lombok.AllArgsConstructor;

/**
 * @author caijy
 * @description TODO
 * @date 2024/1/23 星期二 8:59 下午
 */
@AllArgsConstructor
public class ContextSnapshot {
    private String spanId;

    private String traceId;
}
