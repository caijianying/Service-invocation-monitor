package com.caijy.plugin.inteceptor;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * @author liguang
 * @date 2022/8/1 星期一 1:05 下午
 */
public class DispatcherInteceptor {

    @RuntimeType
    public static Object intercept(@Argument(0) HttpServletRequest request, @SuperCall Callable<?> callable) {
        final StringBuilder in = new StringBuilder();
        if (request.getParameterMap() != null && request.getParameterMap().size() > 0) {
            request.getParameterMap().keySet().forEach(key -> in.append("key=" + key + "_value=" + request.getParameter(key) + ","));
        }
        long agentStart = System.currentTimeMillis();
        try {
            return callable.call();
        } catch (Exception e) {
            System.out.println("Exception :" + e.getMessage());
            return null;
        } finally {
            System.out.println("path:" + request.getRequestURI() + "\n 入参:" + in + "\n 耗时 >>>>：" + (System.currentTimeMillis() - agentStart));
        }
    }
}
