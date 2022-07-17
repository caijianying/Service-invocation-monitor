package com.caijy.plugin.advice;

import java.util.UUID;

import com.caijy.plugin.constants.TraceConstant;
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
        if (filterClassMethods(className, methodName)) {
            return;
        }

        try {
            String traceId = TrackManager.getCurrentSpan();
            if (null == traceId) {
                traceId = UUID.randomUUID().toString();
                TrackContext.setTraceId(traceId);
            }
            TimeCostManager.start(className + "." + methodName);
            String entrySpan = TrackManager.createEntrySpan();
//        System.out.println("链路追踪：" + entrySpan + " " + className + "." + methodName);
        } catch (Throwable e) {
            System.out.println("unknown error has occurred from OnMethodEnter." + e.getMessage());
        }

    }

    @Advice.OnMethodExit()
    public static void exit(@Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        if (filterClassMethods(className, methodName)) {
            return;
        }
        try {
            TimeCostManager.stop(className + "." + methodName);
            String exitSpan = TrackManager.getExitSpan();
//        System.out.println("链路追踪：" + exitSpan + " " + className + "." + methodName + " ==> exit.");
        } catch (Throwable e) {
            System.out.println("unknown error has occurred from OnMethodExit." + e.getMessage());
        }
    }

    public static boolean filterClassMethods(@Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        if (className.indexOf(TraceConstant.TEST_KEY_LOWERCASE) == -1 && className.indexOf(TraceConstant.TEST_KEY_UPPERCASE) == -1) {
            if (className.indexOf(TraceConstant.BY_SPRING_CGLIB) == -1) {
                return true;
            }
            if (TraceConstant.INVOKE.equals(methodName) || TraceConstant.GET_INDEX.equals(methodName)) {
                return true;
            }

            if (TraceConstant.GET_TARGET_CLASS.equals(methodName) || TraceConstant.IS_FROZEN.equals(methodName) ||
                    methodName.indexOf(TraceConstant.SET_CALLBACKS) != -1 || methodName.indexOf(TraceConstant.BIND_CALLBACKS) != -1 ||
                    methodName.indexOf(TraceConstant.STATIC_HOOK) != -1 || methodName.indexOf(TraceConstant.SET_BEAN_FACTORY) != -1 ||
                    methodName.indexOf(TraceConstant.SET_STATIC_CALLBACKS) != -1) {
                return true;
            }
        }
        return false;
    }
}
