package com.caijy.plugin.context;

import java.util.Stack;

/**
 * @Author: caijy
 * @Description
 * @Date: 2022/2/28 星期一 8:20 下午
 */
public class TrackManager {

    private static final ThreadLocal<Stack<String>> track = new ThreadLocal<Stack<String>>();

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
            TimeCostManager.summary();
        }
        return pop;
    }

    public static String getCurrentSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static Integer getTrackSize() {
        return track.get() == null ? 0 : track.get().size();
    }

}


