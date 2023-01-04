package com.caijy.agent.core.plugin.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liguang
 * @date 2022/12/21 星期三 4:47 下午
 */
public class RuntimeContext {
    private Map<String, Object> contextMap = new ConcurrentHashMap<>();

    public void set(String k, Object v) {
        contextMap.put(k, v);
    }

    public Object get(String k) {
        return contextMap.get(k);
    }
}
