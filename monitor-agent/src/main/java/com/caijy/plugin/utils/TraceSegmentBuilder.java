package com.caijy.plugin.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollectionUtil;
import com.caijy.plugin.TraceSegment;
import com.caijy.plugin.TraceSegmentModel;
import com.google.common.collect.Lists;

/**
 * @author liguang
 * @date 2022/8/17 星期三 7:49 下午
 */
public class TraceSegmentBuilder {

    private static InheritableThreadLocal<List<TraceSegmentModel>> traceSegmentThreadLocal
        = new InheritableThreadLocal<>();

    private static InheritableThreadLocal<Stack<TraceSegmentModel>> stackLocal
        = new InheritableThreadLocal<>();

    public static void add(TraceSegmentModel model) {
        if (CollectionUtil.isEmpty(traceSegmentThreadLocal.get())) {
            traceSegmentThreadLocal.set(Lists.newCopyOnWriteArrayList());
        }
        traceSegmentThreadLocal.get().add(model);

        Stack stack = stackLocal.get();
        if (Objects.isNull(stack)) {
            stack = new Stack();
        }
        stack.push(model);
        stackLocal.set(stack);
    }

    public static void clear() {
        traceSegmentThreadLocal.get().clear();
        stackLocal.get().clear();
    }

    public static TraceSegment buildTraceSegment() {
        if (CollectionUtil.isEmpty(traceSegmentThreadLocal.get())) {
            return null;
        }
        List<TraceSegmentModel> segmentModelList = traceSegmentThreadLocal.get();
        System.out.println(JSON.toJSONString(segmentModelList));
        return doBuildTraceSegment(segmentModelList);
    }

    static TraceSegment doBuildTraceSegment(List<TraceSegmentModel> segmentModelList) {
        Stack<TraceSegmentModel> stack = stackLocal.get();

        TraceSegment root = new TraceSegment();
        while (stack.size() > 0) {
            TraceSegmentModel topModel = stack.pop();
            if (stack.isEmpty()){
                break;
            }
            TraceSegmentModel nextModel = stack.pop();
            if (0 == topModel.getProcessFlag()) {
                TraceSegment segment = new TraceSegment();
                segment.setMethodName(topModel.getMethodName());
                List<TraceSegment> children = root.getChildren();
                children = CollectionUtil.isEmpty(children) ? Lists.newArrayList() : children;
                children.add(segment);
                root.setChildren(children);

                if (1 == nextModel.getProcessFlag()) {
                    // do nothing
                } else {
                    root = segment;
                }
            }
        }
        return root;
    }
}
