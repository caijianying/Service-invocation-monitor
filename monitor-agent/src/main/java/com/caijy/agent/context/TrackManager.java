package com.caijy.agent.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import cn.hutool.core.collection.CollectionUtil;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.enums.ConsoleColorEnum;
import com.caijy.agent.model.TraceSegment;
import com.caijy.agent.utils.ConfigBanner;
import com.caijy.agent.utils.TraceSegmentBuilder;

/**
 * @Author: caijy
 * @Description
 * @Date: 2022/2/28 星期一 8:20 下午
 */
public class TrackManager {

    private static final InheritableThreadLocal<Stack<String>> track = new InheritableThreadLocal<Stack<String>>();

    private static String createSpan() {
        Stack<String> stack = track.get();
        if (stack == null) {
            stack = new Stack<>();
            track.set(stack);
        }
        String traceId;
        if (stack.isEmpty()) {
            traceId = TrackContext.getTraceId();
            if (traceId == null) {
                traceId = "nvl";
                TrackContext.setTraceId(traceId);
            }
        } else {
            traceId = stack.peek();
            TrackContext.setTraceId(traceId);
        }
        return traceId;
    }

    public static String createEntrySpan() {
        String span = createSpan();
        Stack<String> stack = track.get();
        stack.push(span);
        return span;
    }

    public static String getExitSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            TrackContext.clear();
            return null;
        }
        String pop = stack.pop();
        if (stack.size() == 0) {
            TraceSegment traceSegment = TraceSegmentBuilder.buildTraceSegment();
            setDepth(traceSegment, 0);
            StringBuilder builder = new StringBuilder();
            append(builder, traceSegment);
            System.out.println(ConfigBanner.toColorString(ConsoleColorEnum.GREEN, builder));
            TraceSegmentBuilder.clear();
        }
        return pop;
    }

    private static void setDepth(TraceSegment parent, int depth) {
        if (parent != null && CollectionUtil.isNotEmpty(parent.getChildren())) {
            LinkedList<TraceSegment> segments = parent.getChildren();
            for (TraceSegment traceSegment : segments) {
                traceSegment.setDepth(depth);
                setDepth(traceSegment, depth + 1);
            }
        }
    }

    private static void appendChild(StringBuilder builder, List<TraceSegment> segments) {
        if (CollectionUtil.isNotEmpty(segments)) {
            Long timeCostThreshold = Long.valueOf(
                Config.get(AgentConstant.MONITOR_TIME_COST_THRESHOLD).toString());
            for (TraceSegment traceSegment : segments) {
                for (int i = 0; i < traceSegment.getDepth(); i++) {
                    builder.append("    ");
                }

                String format = String.format("|--- %s [%s] ms\n", traceSegment.getMethodName(),
                    ConfigBanner
                        .toColorString(
                            Long.parseLong(traceSegment.getCostTime()) > timeCostThreshold ? ConsoleColorEnum.RED
                                : ConsoleColorEnum.GREEN, traceSegment.getCostTime()));
                builder.append(format);
                appendChild(builder, traceSegment.getChildren());
            }
        }
    }

    private static void append(StringBuilder builder, TraceSegment traceSegment) {
        builder.append(
            ConfigBanner
                .toColorString(ConsoleColorEnum.CYAN, "|--- " + traceSegment.getMethodName(), "---|") + "\n");
        appendChild(builder, traceSegment.getChildren());
    }

    public static String getCurrentSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }
}

