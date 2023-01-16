package com.caijy.agent.tools.thread;

import java.io.Serializable;

import lombok.Data;

/**
 * @author liguang
 * @date 2023/1/13 星期五 2:52 下午
 */
@Data
public class ThreadSpan implements Serializable {

    private static final long serialVersionUID = 1L;

    private Thread parent;

    private Long threadId;

    private String threadName;

    private String methodName;

    private boolean running;

    private long startTime;

    private long endTime;

    private ThreadSpanHandler handler;

    public ThreadSpan(ThreadSpanHandler handler) {
        this.handler = handler;
    }

    private ThreadSpan methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ThreadSpan parent(Thread parent) {
        this.parent = parent;
        return this;
    }

    public void start() {
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.handler.push(this);
    }

    public void stop(){
        if (this.methodName != null){

        }
    }

    public boolean isRunning() {
        return running;
    }
}
