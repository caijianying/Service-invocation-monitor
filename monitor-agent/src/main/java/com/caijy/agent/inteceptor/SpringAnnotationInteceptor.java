package com.caijy.agent.inteceptor;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.caijy.agent.model.TraceSegmentModel;
import com.caijy.agent.context.TrackContext;
import com.caijy.agent.context.TrackManager;
import com.caijy.agent.utils.IgnoredUtils;
import com.caijy.agent.utils.TraceSegmentBuilder;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * @author liguang
 * @date 2022/8/1 星期一 1:05 下午
 */
public class SpringAnnotationInteceptor {

    public static final ThreadLocal<AtomicInteger> depthLocal = new ThreadLocal<>();

    @RuntimeType
    public static Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @Origin Method method,
        @SuperCall Callable<?> callable) throws Throwable {
        if (IgnoredUtils.ignoredMethods(method.getName())) {
            return callable.call();
        }
        String traceId = TrackManager.getCurrentSpan();
        if (null == traceId) {
            traceId = UUID.randomUUID().toString();
            TrackContext.setTraceId(traceId);
        }
        Object call = null;
        try {
            int depth = increase();
            TrackManager.createEntrySpan();
            TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(1)
                    .methodName(clazz.getName() + "." + method.getName()).depth(depth).build());
            //System.out.println(clazz.getName() + "." + method.getName()+" >>> enter.");
            long startMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            call = callable.call();
            long stopMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 方法退出即减一 保证depth的准确性
            decrease();
            TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(0).methodName(
                    clazz.getName() + "." + method.getName()).costTimeStamp(stopMilli - startMilli).depth(depth).build());
            //System.out.println(clazz.getName() + "." + method.getName()+" >>> exit.");
            TrackManager.getExitSpan();
        }catch (Throwable e){
            System.out.println("Service Invocation Monitor | ERROR: An Error occurred! reason:"+e.getMessage());
        }

        return call;
    }

    private static int increase(){
        AtomicInteger depth = depthLocal.get();
        if (depth == null){
            depth = new AtomicInteger(0);
        }
        int result = depth.incrementAndGet();
        depthLocal.set(depth);
        return result;
    }

    private static int decrease(){
        AtomicInteger depth = depthLocal.get();
        if (depth == null){
            depth = new AtomicInteger(0);
        }
        int result = depth.decrementAndGet();
        depthLocal.set(depth);
        return result;
    }
}
