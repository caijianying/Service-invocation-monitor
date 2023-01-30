package com.caijy.agent.core.utils;

import java.util.*;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.caijy.agent.core.model.TraceSegment;
import com.caijy.agent.core.model.TraceSegmentModel;
import com.google.common.collect.Lists;

/**
 * @author liguang
 * @date 2022/8/17 星期三 7:49 下午
 */
public class TraceSegmentBuilder {
    static Log log = LogFactory.getCurrentLogFactory().createLog(TraceSegmentBuilder.class);

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
        TraceSegment root = doBuildTraceSegment();
        return root;
    }

    static TraceSegment doBuildTraceSegment() {
        Stack<TraceSegmentModel> stack = stackLocal.get();
        TraceSegment root = new TraceSegment();
        root.setMethodName("Service Invocation Monitor | Result ");

        TraceSegment parent = root;

        while (stack.size() > 0) {
            TraceSegmentModel topModel = stack.pop();
            if (stack.isEmpty()) {
                break;
            }
            if (0 == topModel.getProcessFlag()) {
                TraceSegment segment = new TraceSegment();
                segment.setMethodName(topModel.getMethodName());
                segment.setComponentName(
                    StrUtil.isBlank(topModel.getComponentName()) ? "" : topModel.getComponentName());
                segment.setCostTime(String.valueOf(topModel.getCostTimeStamp()));
                // 缺省值
                segment.setDepth(0);
                appendChild(segment, parent);
                parent = segment;
            } else {
                if (parent != null && parent.getParent() != null) {
                    parent = parent.getParent();
                }
            }
        }
        return root;
    }

    private static void appendChild(TraceSegment current, TraceSegment parent) {
        current.setParent(parent);
        LinkedList<TraceSegment> children = parent.getChildren();
        children = CollectionUtil.isEmpty(children) ? Lists.newLinkedList() : children;
        children.addFirst(current);
        parent.setChildren(children);
    }
}
