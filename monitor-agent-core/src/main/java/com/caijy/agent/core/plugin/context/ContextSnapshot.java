package com.caijy.agent.core.plugin.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author caijy
 * @description TODO
 * @date 2024/1/23 星期二 8:59 下午
 */
@Getter
@AllArgsConstructor
public class ContextSnapshot {
    private String spanId;

    private String traceId;
}
