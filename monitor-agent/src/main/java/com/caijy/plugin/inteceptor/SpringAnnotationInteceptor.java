package com.caijy.plugin.inteceptor;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import com.caijy.plugin.TraceSegmentModel;
import com.caijy.plugin.constants.ClassFilterConstants;
import com.caijy.plugin.context.TimeCostManager;
import com.caijy.plugin.context.TrackContext;
import com.caijy.plugin.context.TrackManager;
import com.caijy.plugin.utils.IgnoredUtils;
import com.caijy.plugin.utils.TraceSegmentBuilder;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * @author liguang
 * @date 2022/8/1 星期一 1:05 下午
 */
public class SpringAnnotationInteceptor {

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
        TrackManager.createEntrySpan();
        TimeCostManager.start(clazz.getName() + "." + method.getName());
        TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(1).methodName(clazz.getName() + "." + method.getName()).build());
        //System.out.println(clazz.getName() + "." + method.getName()+" >>> enter.");
        Object call = callable.call();
        TimeCostManager.stop(clazz.getName() + "." + method.getName());
        TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(0).methodName(clazz.getName() + "." + method.getName()).build());
        //System.out.println(clazz.getName() + "." + method.getName()+" >>> exit.");
        TrackManager.getExitSpan();
        return call;
    }
}
