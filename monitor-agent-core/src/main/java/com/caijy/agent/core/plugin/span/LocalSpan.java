package com.caijy.agent.core.plugin.span;

import com.caijy.agent.core.trace.ComponentDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caijy
 * @description isRoot, traceId, component, name, parentSpanId spanId
 * @date 2024/1/26 星期五 5:05 下午
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LocalSpan {
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private Boolean isRoot;
    private ComponentDefine componentDefine;
    private String operatorName;
}
