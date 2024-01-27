package com.caijy.agent.test.models;

import cn.hutool.core.lang.UUID;
import com.caijy.agent.core.enums.ConsoleColorEnum;
import com.caijy.agent.core.model.TraceSegment;
import com.caijy.agent.core.plugin.context.RuntimeContext;
import com.caijy.agent.core.plugin.context.TrackManager;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.core.utils.ConfigBanner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author caijy
 * @description
 * @date 2023/11/4 星期六 4:19 下午
 */
@Slf4j
public class InvocationContextV1 {

    private static final ThreadLocal<String> LOCAL_TRACE_ID = new ThreadLocal<>();

    private final Stack<String> invokeStack = new Stack<>();

    private static final ThreadLocal<String> LOCAL_SPAN_ID = new ThreadLocal<>();

    public void enter(ComponentDefine component, String name) {
//        String traceId = LOCAL_TRACE_ID.get();
//        boolean isRoot = false;
//        if (traceId == null) {
//            if (LastTraceId.INSTANCE.getId() == null) {
//                isRoot = true;
//                traceId = UUID.fastUUID().toString();
//                LastTraceId.INSTANCE.setId(traceId);
//            }
//            LOCAL_TRACE_ID.set(LastTraceId.INSTANCE.getId());
//        }
//        String spanId = RuntimeContext.enter(isRoot, LOCAL_TRACE_ID.get(), component, name, ParentSpanId.INSTANCE.getId());
//        log.info("InvocationContextV1.enter:spanId={},name:{}", spanId, name);
//        LOCAL_SPAN_ID.set(spanId);
//        CurrentSpanId.INSTANCE.setId(spanId);
//        invokeStack.push(spanId);
//
//        if (isRoot) {
//            CurrentLink.INSTANCE.setRootSpanId(spanId);
//        }
    }

    public boolean isRoot() {
        return LastTraceId.INSTANCE.getId() == null;
    }

    public synchronized void exit() {
//        RuntimeContext.exit(LOCAL_SPAN_ID.get(),LOCAL_TRACE_ID.get());
//        if (invokeStack.size() > 0) {
//            invokeStack.pop();
//        }
//        if (invokeStack.size() == 1 && CurrentLink.INSTANCE.isEnd(invokeStack.pop())) {
//            printInformation();
//            clear();
//        }
//        log.info("InvocationContextV1.exit,invokeStack: {}", invokeStack);
//        invokeStack.forEach(s -> System.out.print(s + ";"));
    }

    private void clear() {
        LastTraceId.INSTANCE.setId(null);
        CurrentSpanId.INSTANCE.setId(null);
        ParentSpanId.INSTANCE.setId(null);
        LOCAL_TRACE_ID.remove();
    }

    private void printInformation() {
//        TraceSegment traceSegment = RuntimeContext.getTraceSegment();
//        StringBuilder builder = new StringBuilder();
//        TrackManager.append(builder, traceSegment, new ArrayList<>());
//        String colorString = ConfigBanner.toColorString(ConsoleColorEnum.GREEN, builder);
//        System.out.println(colorString);
    }


    public void prepareCrossThread() {
        LastTraceId.INSTANCE.setId(LOCAL_TRACE_ID.get());
        ParentSpanId.INSTANCE.setId(CurrentSpanId.INSTANCE.getId());
    }

    @Data
    static class LastTraceId {
        private String id;
        static LastTraceId INSTANCE = new LastTraceId();
    }

    @Data
    static class CurrentSpanId {
        private Map<Long, String> threadSpanIdMap = new HashMap<>();
        static CurrentSpanId INSTANCE = new CurrentSpanId();

        public void setId(String id) {
            threadSpanIdMap.put(Thread.currentThread().getId(), id);
        }

        public String getId() {
            return threadSpanIdMap.get(Thread.currentThread().getId());
        }
    }

    @Data
    static class ParentSpanId {
        private String id;
        static ParentSpanId INSTANCE = new ParentSpanId();
    }

    @Data
    static class CurrentLink {
        private String rootSpanId;
        static CurrentLink INSTANCE = new CurrentLink();

        private boolean isEnd(String spanId) {
            return rootSpanId.equals(spanId);
        }
    }
}
