package com.caijy.agent.inteceptor;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:54 下午
 */
public class MethodInvocationContext {

    private String traceId;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
