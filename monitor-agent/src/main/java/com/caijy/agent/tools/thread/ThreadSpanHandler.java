package com.caijy.agent.tools.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liguang
 * @date 2023/1/13 星期五 3:06 下午
 */
public class ThreadSpanHandler {

    List<ThreadSpan> activeSpans = new ArrayList<>(0);

    public void push(ThreadSpan span) {
        activeSpans.add(span);
    }

    public void stop(ThreadSpan span) {
        if (activeSpans.isEmpty()){
            return;
        }
        activeSpans.stream().filter(
            activeSpan -> activeSpan.getThreadId().equals(span.getThreadId()) && activeSpan.getMethodName()
                .equals(span.getMethodName())).forEach(activeSpan -> {
            activeSpan.setEndTime(span.getEndTime());
            activeSpan.setRunning(false);
        });
        boolean finish = activeSpans.stream().allMatch(activeSpan -> activeSpan.isRunning() == false);
        if (finish) {
            // sort

        }
    }

}
