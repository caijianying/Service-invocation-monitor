package com.caijy.plugin.context;

/**
 * @author liguang
 * @date 2022/7/5 星期二 8:51 下午
 */
public class TrackContext {
    private static final ThreadLocal<String> trackLocal = new ThreadLocal<String>();

    public static void clear(){
        trackLocal.remove();
    }

    public static String getTraceId(){
        return trackLocal.get();
    }

    public static void setTraceId(String traceId){
        trackLocal.set(traceId);
    }
}
