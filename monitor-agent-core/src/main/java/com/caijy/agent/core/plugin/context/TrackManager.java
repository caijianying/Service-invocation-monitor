package com.caijy.agent.core.plugin.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import cn.hutool.core.collection.CollectionUtil;
import com.caijy.agent.core.config.Config;
import com.caijy.agent.core.console.TraceConsoleDTO;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.core.enums.ConsoleColorEnum;
import com.caijy.agent.core.model.TraceSegment;
import com.caijy.agent.core.utils.ConfigBanner;
import com.caijy.agent.core.utils.FileCache;
import com.caijy.agent.core.utils.TraceSegmentBuilder;
import com.google.common.collect.Lists;

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
            List<TraceConsoleDTO> consoleDTOList = Lists.newArrayList();
            StringBuilder builder = new StringBuilder();
            append(builder, traceSegment, consoleDTOList);
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

    private static void appendChild(StringBuilder builder, List<TraceSegment> segments,
        List<TraceConsoleDTO> consoleDTOList) {
        if (CollectionUtil.isNotEmpty(segments)) {
            Long timeCostThreshold = Long.valueOf(
                Config.get(AgentConstant.MONITOR_TIME_COST_THRESHOLD).toString());
            for (TraceSegment traceSegment : segments) {
                String preText = "";
                for (int i = 0; i < traceSegment.getDepth(); i++) {
                    builder.append("    ");
                    preText += "    ";
                }
                preText += String.format("|--- %s [", traceSegment.getMethodName());
                String midText = traceSegment.getCostTime();
                String midColor = Long.parseLong(traceSegment.getCostTime()) > timeCostThreshold
                    ? AgentConstant.CONSOLE_COLOR_RED : AgentConstant.CONSOLE_COLOR_GREEN;
                String tailText = "] ms\n";
                String format = String.format("|--- %s [%s] ms\n", traceSegment.getMethodName(),
                    ConfigBanner
                        .toColorString(
                            Long.parseLong(traceSegment.getCostTime()) > timeCostThreshold ? ConsoleColorEnum.RED
                                : ConsoleColorEnum.GREEN, traceSegment.getCostTime()));
                builder.append(format);
                consoleDTOList.add(new TraceConsoleDTO(null, preText));
                consoleDTOList.add(new TraceConsoleDTO(midColor, midText));
                consoleDTOList.add(new TraceConsoleDTO(null, tailText));
                appendChild(builder, traceSegment.getChildren(), consoleDTOList);
            }
        }
    }

    private static void append(StringBuilder builder, TraceSegment traceSegment, List<TraceConsoleDTO> consoleDTOList) {
        builder.append(
            ConfigBanner
                .toColorString(ConsoleColorEnum.CYAN, "|--- " + traceSegment.getMethodName(), "---|") + "\n");
        consoleDTOList.add(
            new TraceConsoleDTO(AgentConstant.CONSOLE_COLOR_CYAN, "|--- " + traceSegment.getMethodName() + "---|\n"));
        appendChild(builder, traceSegment.getChildren(), consoleDTOList);
    }

    public static String getCurrentSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }
}


