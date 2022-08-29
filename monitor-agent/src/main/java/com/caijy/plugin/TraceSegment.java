package com.caijy.plugin;

import java.io.Serializable;
import java.util.List;

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
     * 耗时
     **/
    private String costTime;

    /**
     * 子节点
     **/
    private TraceSegment childNode;

    /**
     * 子方法
     **/
    private List<TraceSegment> children;

}
