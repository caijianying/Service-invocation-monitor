package com.caijy.agent.core.plugin.context;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.caijy.agent.core.boot.BootService;
import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.console.TraceConsoleDTO;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.core.enums.ConsoleColorEnum;
import com.caijy.agent.core.model.TraceSegment;
import com.caijy.agent.core.plugin.span.LocalSpan;
import com.caijy.agent.core.trace.ComponentDefine;
import com.caijy.agent.core.utils.ConfigBanner;
import com.caijy.agent.core.utils.FileCache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author caijy
 * @description 上下文管理
 * @date 2024/1/26 星期五 4:04 下午
 */
@Slf4j
public class ContextManager implements BootService {

    private static final LinkedList<String> activeSpanIdMap = new LinkedList<>();

    private static final ThreadLocal<Stack> STACK_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<String> LOCAL_TRACE_ID = new ThreadLocal<>();

    public static void createSpan(ComponentDefine component, String operatorName) {
        createSpan(component, operatorName, null);
    }

    public static void createSpan(ComponentDefine component, String operatorName, ContextSnapshot snapshot) {
        long start = System.currentTimeMillis();
        LocalSpan localSpan = LocalSpan.builder()
                .isRoot(isRoot(snapshot))
                .traceId(getOrCreateTraceId(snapshot))
                .operatorName(operatorName)
                .componentDefine(component)
                .spanId(UUID.randomUUID().toString())
                .parentSpanId(getParentSpanId(snapshot))
                .build();
        getStack().push(localSpan.getSpanId());
        activeSpanIdMap.addLast(localSpan.getSpanId());
        startSpan(localSpan, start);
//        log.debug("createSpan>>:{}",JSONUtil.toJsonStr(localSpan));
    }

    private static Stack<String> getStack() {
        if (STACK_THREAD_LOCAL.get() == null) {
            Stack<String> activeSpanIdStack = new Stack<>();
            STACK_THREAD_LOCAL.set(activeSpanIdStack);
        }
        return STACK_THREAD_LOCAL.get();
    }

    private static void startSpan(LocalSpan localSpan, Long start) {

        RuntimeContext.TraceModel model = new RuntimeContext.TraceModel();
        model.setRoot(localSpan.getIsRoot());
        model.setComponent(localSpan.getComponentDefine().name());
        model.setName(localSpan.getOperatorName());
        model.setStart(start);
        model.setTraceId(localSpan.getTraceId());
        model.setSpanId(localSpan.getSpanId());
        model.setParentSpanId(localSpan.getParentSpanId());

        if (localSpan.getIsRoot()) {
            model.setParentSpanId("0");
        }
        RuntimeContext.registerTraceInfo(model);
    }

    public static void stopSpan() {
        String traceId = LOCAL_TRACE_ID.get();
        if (getStack().size() > 0) {
            String spanId = getStack().pop();
            RuntimeContext.exit(traceId, spanId);
            activeSpanIdMap.remove(spanId);
        }
        if (activeSpanIdMap.isEmpty()) {
            printInformation(traceId);
            clear(traceId);
        }
//        log.debug("InvocationContextV1.exit,invokeStack: {}", activeSpanIdMap);
    }

    private static void clear(String traceId) {
        if (traceId != null) {
            RuntimeContext.clearTrace(traceId);
        }
        LOCAL_TRACE_ID.remove();
        activeSpanIdMap.clear();
        STACK_THREAD_LOCAL.remove();
    }

    public static ContextSnapshot capture() {
        return new ContextSnapshot(activeSpanIdMap.getLast(), LOCAL_TRACE_ID.get());
    }

    public static boolean isRoot() {
        return LOCAL_TRACE_ID.get() == null;
    }

    public static boolean isRoot(ContextSnapshot contextSnapshot) {
        return LOCAL_TRACE_ID.get() == null && contextSnapshot == null;
    }

    private static String getParentSpanId(ContextSnapshot contextSnapshot) {
        if (contextSnapshot != null) {
            return contextSnapshot.getSpanId();
        }
        if (!getStack().isEmpty()) {
            return getStack().peek();
        }
        return null;
    }

    public static boolean isActive() {
        return !activeSpanIdMap.isEmpty();
    }

    private static void printInformation(String traceId) {
        List<TraceConsoleDTO> consoleDTOList = Lists.newArrayList();
        TraceSegment traceSegment = RuntimeContext.getTraceSegment(traceId);
        StringBuilder builder = new StringBuilder();
        TrackManager.append(builder, traceSegment, consoleDTOList);
        String colorString = ConfigBanner.toColorString(ConsoleColorEnum.GREEN, builder);
        System.out.println(colorString);
        int sampleRate = Integer.valueOf(Config.get(AgentConstant.MONITOR_SAMPLE_RATE).toString());
        Object path = Config.get(AgentConstant.MONITOR_AGENT_PATH);
        if (path != null) {
            if (consoleDTOList.size() <= sampleRate) {
                FileCache.appendLines(Config.get(AgentConstant.MONITOR_AGENT_PATH).toString(),
                        Config.get(AgentConstant.MONITOR_PROJECT_CODE).toString(), consoleDTOList);
            }
        }
    }

    public static String getOrCreateTraceId(ContextSnapshot contextSnapshot) {
        String traceId = LOCAL_TRACE_ID.get();
        if (traceId == null) {
            if (contextSnapshot != null) {
                traceId = contextSnapshot.getTraceId();
            } else {
                traceId = UUID.fastUUID().toString();
            }
            LOCAL_TRACE_ID.set(traceId);
        }
        return traceId;
    }

    @Override
    public void boot() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {

    }
}
