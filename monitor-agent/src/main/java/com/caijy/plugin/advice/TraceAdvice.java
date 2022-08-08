package com.caijy.plugin.advice;

import java.util.UUID;

import com.caijy.plugin.context.TimeCostManager;
import com.caijy.plugin.context.TrackContext;
import com.caijy.plugin.context.TrackManager;
import net.bytebuddy.asm.Advice;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:50 下午
 */
public class TraceAdvice {



    @Advice.OnMethodEnter()
    public static void enter(@Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        if (className.indexOf("test") == -1 && className.indexOf("Test") == -1){
            if (className.indexOf("BySpringCGLIB") == -1) {
                return;
            }
            if ("invoke".equals(methodName) || "getIndex".equals(methodName)) {
                return;
            }
        }

        String traceId = TrackManager.getCurrentSpan();
        if (null == traceId) {
            traceId = UUID.randomUUID().toString();
            TrackContext.setTraceId(traceId);
        }
        String entrySpan = TrackManager.createEntrySpan();
        TimeCostManager.start(className + "." + methodName);
        System.out.println("链路追踪：" + entrySpan + " " + className + "." + methodName);

    }

    @Advice.OnMethodExit()
    public static void exit(@Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        if (className.indexOf("test") == -1 && className.indexOf("Test") == -1){
            if (className.indexOf("BySpringCGLIB") == -1) {
                return;
            }
            if ("invoke".equals(methodName) || "getIndex".equals(methodName)) {
                return;
            }
        }
        TimeCostManager.stop(className + "." + methodName);
        String exitSpan = TrackManager.getExitSpan();
        System.out.println("链路追踪：" + exitSpan + " " + className + "." + methodName + " ==> exit.");
    }

}
