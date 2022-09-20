package com.caijy.plugin.utils;

import java.util.*;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.caijy.plugin.model.TraceSegment;
import com.caijy.plugin.model.TraceSegmentModel;
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
        //System.out.println("buildTraceSegment >>>: " + JSON.toJSONString(root));
        return root;
    }

    static TraceSegment doBuildTraceSegment() {
        Stack<TraceSegmentModel> stack = stackLocal.get();
        //log.info("doBuildTraceSegment >>>: {}" , JSON.toJSONString(stack));
        TraceSegment root = new TraceSegment();
        root.setMethodName("Service Invocation Monitor | Result ");

        TraceSegment parent = root;

        int lastExitDepth = 0;
        while (stack.size() > 0) {
            TraceSegmentModel topModel = stack.pop();
            if (stack.isEmpty()) {
                break;
            }
            if (0 == topModel.getProcessFlag()) {
                lastExitDepth = topModel.getDepth();
                TraceSegment segment = new TraceSegment();
                segment.setMethodName(topModel.getMethodName());
                segment.setCostTime(String.valueOf(topModel.getCostTimeStamp()));
                segment.setDepth(topModel.getDepth());
                appendChild(segment, parent);
                parent = segment;
            } else {
//                System.out.println(String.format("doBuildTraceSegment.enter >>>: %s,last: %s,current: %s", topModel.getMethodName(), lastExitDepth, topModel.getDepth()));
                while (lastExitDepth != topModel.getDepth()-1) {
                    if (parent != null && parent.getParent()!=null) {
                        parent = parent.getParent();
//                        System.out.println("doBuildTraceSegment.enter.while: "+JSON.toJSONString(parent));
                        lastExitDepth = parent.getDepth();
                    } else {
                        break;
                    }
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
