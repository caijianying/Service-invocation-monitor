package com.caijy.plugin.model;

import java.io.Serializable;
import java.util.LinkedList;

import lombok.Data;

/**
 * @author liguang
 * @date 2022/8/17 星期三 7:44 下午
 */
@Data
public class TraceSegment implements Serializable {

    /**
     * 全方法名
     **/
    private String methodName;

    /**
     * enter
     **/
    private Long enterTimeStamp;

    /**
     * exit
     **/
    private Long exitTimeStamp;

    /**
     * 耗时
     **/
    private String costTime;

    /**
     * 子方法
     **/
    private LinkedList<TraceSegment> children;

    /**
     * 当前节点深度
     **/
    private int depth;

    /**
     * 父节点
     **/
    private TraceSegment parent;

}
