package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.caijy.agent.context.RuntimeContext;
import com.caijy.agent.context.TrackContext;
import com.caijy.agent.context.TrackManager;
import com.caijy.agent.model.TraceSegmentModel;
import com.caijy.agent.utils.IgnoredUtils;
import com.caijy.agent.utils.TraceSegmentBuilder;

/**
 * @author liguang
 * @date 2022/12/21 星期三 3:57 下午
 */
public class SpringInterceptor implements MethodAroundInterceptor {

    public static final ThreadLocal<AtomicInteger> depthLocal = new ThreadLocal<>();

    ThreadLocal<RuntimeContext> tl = new ThreadLocal<RuntimeContext>();

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {

        //if (IgnoredUtils.ignoredMethods(method.getName())) {
        //    return;
        //}
        //String traceId = TrackManager.getCurrentSpan();
        //if (null == traceId) {
        //    traceId = UUID.randomUUID().toString();
        //    TrackContext.setTraceId(traceId);
        //}
        //
        //Long startMilli = null;
        //Integer depth = null;
        //try {
        //    depth = increase();
        //    TrackManager.createEntrySpan();
        //    TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(1)
        //        .methodName(clazz.getName() + "." + method.getName()).depth(depth).build());
        //    startMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //} catch (Throwable e) {
        //    System.out.println(
        //        "SpringInterceptor.beforeMethod | ERROR: before method invoke An Error occurred! reason:" + e.getMessage());
        //    e.printStackTrace();
        //} finally {
        //    RuntimeContext runtimeContext = new RuntimeContext();
        //    runtimeContext.set("startMilli", startMilli);
        //    runtimeContext.set("depth", depth);
        //    tl.set(runtimeContext);
        //}
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInvocationContext context) throws Throwable {
        //try {
        //    long stopMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //    // 方法退出即减一 保证depth的准确性
        //    decrease();
        //    TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(0).methodName(
        //        clazz.getName() + "." + method.getName()).costTimeStamp(
        //        stopMilli - Long.valueOf(tl.get().get("startMilli").toString()))
        //        .depth(Integer.valueOf(tl.get().get("depth").toString())).build());
        //    TrackManager.getExitSpan();
        //} catch (Throwable e) {
        //    System.out.println(
        //        "SpringInterceptor.afterMethod | ERROR: after method invoke An Error occurred! reason:" + e.getMessage());
        //    e.printStackTrace();
        //}

    }

    private static int increase() {
        AtomicInteger depth = depthLocal.get();
        if (depth == null) {
            depth = new AtomicInteger(0);
        }
        int result = depth.incrementAndGet();
        depthLocal.set(depth);
        return result;
    }

    private static int decrease() {
        AtomicInteger depth = depthLocal.get();
        if (depth == null) {
            depth = new AtomicInteger(0);
        }
        int result = depth.decrementAndGet();
        depthLocal.set(depth);
        return result;
    }
}
