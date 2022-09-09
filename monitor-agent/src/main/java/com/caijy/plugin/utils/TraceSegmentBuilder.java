package com.caijy.plugin.utils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollectionUtil;
import com.caijy.plugin.model.TraceSegment;
import com.caijy.plugin.model.TraceSegmentModel;
import com.google.common.collect.Lists;

/**
 * @author liguang
 * @date 2022/8/17 星期三 7:49 下午
 */
public class TraceSegmentBuilder {

    private static InheritableThreadLocal<Stack<TraceSegmentModel>> stackLocal
        = new InheritableThreadLocal<>();

    public static void add(TraceSegmentModel model) {
        Stack stack = stackLocal.get();
        if (Objects.isNull(stack)) {
            stack = new Stack();
        }
        stack.push(model);
        stackLocal.set(stack);
    }

    public static void clear() {
        stackLocal.get().clear();
    }

    public static TraceSegment buildTraceSegment() {
        return doBuildTraceSegment();
    }

    static TraceSegment doBuildTraceSegment() {
        Stack<TraceSegmentModel> stack = stackLocal.get();
        System.out.println("doBuildTraceSegment >>>: "+JSON.toJSONString(stack));
        TraceSegment root = new TraceSegment();
        root.setMethodName("Service Invocation Monitor | Result ");

        TraceSegment parent = root;

        while (stack.size() > 0) {
            TraceSegmentModel topModel = stack.pop();
            if (stack.isEmpty()) {
                break;
            }
            TraceSegmentModel nextModel = stack.pop();
            if (0 == topModel.getProcessFlag()) {
                TraceSegment segment = new TraceSegment();
                segment.setMethodName(topModel.getMethodName());
                segment.setCostTime(String.valueOf(topModel.getCostTimeStamp()));
                appendChild(segment, parent);

                if (1 == nextModel.getProcessFlag()) {
                    // do nothing

                } else {
                    TraceSegment current = new TraceSegment();
                    current.setMethodName(nextModel.getMethodName());
                    current.setCostTime(String.valueOf(nextModel.getCostTimeStamp()));
                    parent = segment;
                    appendChild(current, parent);
                    parent = current;
                }
            }
        }
        return root;
    }

    private static void appendChild(TraceSegment current, TraceSegment parent) {
        LinkedList<TraceSegment> children = parent.getChildren();
        children = CollectionUtil.isEmpty(children) ? Lists.newLinkedList() : children;
        children.addFirst(current);
        parent.setChildren(children);
    }
}
