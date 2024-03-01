package com.caijy.agent.core.plugin.context;

import cn.hutool.json.JSONUtil;
import com.caijy.agent.core.MonitorException;
import com.caijy.agent.core.model.TraceSegment;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author liguang
 * @date 2022/12/21 星期三 4:47 下午
 */
@Slf4j
public class RuntimeContext {
    private final ThreadLocal<RuntimeContext> contextThreadLocal;
    private Map<String, Object> contextMap = new ConcurrentHashMap<>();
    /**
     * k-> traceId v-> traceModels
     **/
    private static Map<String, List<TraceModel>> traceModelsMap = new ConcurrentHashMap<>();

    private static final ThreadLocal<String> LOCAL_TRACE_ID = new ThreadLocal<>();

    private static final ThreadLocal<String> LOCAL_SPAN_ID = new ThreadLocal<>();

    public static TraceSegment getTraceSegment(String traceId) {
        List<TraceModel> traceInfos = getTraceInfos(traceId);
//        log.debug("getTraceSegment>>: {}", JSONUtil.toJsonStr(traceInfos));
        try {
            TraceModel bannerModel = new TraceModel();
            bannerModel.setName("Service Invocation Monitor | Result ");
            bannerModel.setSpanId("0");
            bannerModel.setCostTime(0L);
            traceInfos.add(bannerModel);
            TraceSegment rootSegment = fillingEachSegment(bannerModel, 0, traceInfos);
//            log.debug("rootSegment:{}", JSONUtil.toJsonStr(rootSegment));
            return rootSegment;
        } catch (Throwable ex) {
            MonitorException.error(RuntimeContext.class, ex);
        } finally {
            traceInfos.clear();
        }
        return null;
    }

    private static TraceSegment fillingEachSegment(TraceModel model, int depth, List<TraceModel> traceModels) {
        TraceSegment segment = new TraceSegment();
        segment.setComponentName(model.getComponent());
        segment.setCostTime(model.getCostTime() == null ? null : model.getCostTime().toString());
        segment.setMethodName(model.getName());
        segment.setEnterTimeStamp(model.getStart());
        segment.setExitTimeStamp(model.getEnd());
        segment.setDepth(depth);
        String spanId = model.getSpanId();
        List<TraceModel> models = traceModels.stream().filter(child -> spanId.equals(child.getParentSpanId())).collect(Collectors.toList());
        if (models != null) {
            List<TraceSegment> children = models.stream().map(m -> fillingEachSegment(m, depth + 1, traceModels)).collect(Collectors.toList());
            segment.setChildren(new LinkedList<>(children));
        }
        return segment;
    }

    public void set(String k, Object v) {
        contextMap.put(k, v);
    }

    public Object get(String k) {
        return contextMap.get(k);
    }

    public RuntimeContext(ThreadLocal<RuntimeContext> contextThreadLocal) {
        this.contextThreadLocal = contextThreadLocal;
    }

    public static void registerTraceInfo(TraceModel model) {
        List<TraceModel> traceInfos = getTraceInfos(model.getTraceId());
        traceInfos.add(model);
    }

    private static List<TraceModel> getTraceInfos(String traceId) {
        if (traceId == null) {
            return Lists.newArrayList();
        }
        List<TraceModel> traceModels = traceModelsMap.get(traceId);
        if (traceModels == null) {
            traceModels = new ArrayList<>();
            traceModelsMap.put(traceId, traceModels);
        }
//        log.debug("getTraceInfos >>: {} ", JSONUtil.toJsonStr(traceModelsMap));
        return traceModels;
    }


    public static void exit(String traceId, String spanId) {
        if (traceId != null && spanId != null) {
            long end = System.currentTimeMillis();
            List<TraceModel> traceInfos = getTraceInfos(traceId);
            for (TraceModel m : traceInfos) {
                if (m.getSpanId().equals(spanId)) {
                    m.end(end).calcCostTime();
                    break;
                }
            }
        }
    }

    public static void clearTrace(String traceId) {
        traceModelsMap.remove(traceId);
    }

    @Data
    static class TraceModel {
        private String traceId;

        private boolean isRoot;

        private String spanId;

        private String parentSpanId;

        private String name;

        private String component;

        private Long start;

        private Long end;

        private Long costTime;

        private int depth;

        private TraceModel end(Long end) {
            this.end = end;
            return this;
        }

        private void calcCostTime() {
            this.costTime = end - start;
        }
    }
}
