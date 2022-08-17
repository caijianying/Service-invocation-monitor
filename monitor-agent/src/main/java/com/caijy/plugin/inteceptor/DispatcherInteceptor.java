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
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        }
    }
}
