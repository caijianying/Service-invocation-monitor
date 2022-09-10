package com.caijy.plugin.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private static InheritableThreadLocal<List<TraceSegmentModel>> listLocal
            = new InheritableThreadLocal<>();

    public static void add(TraceSegmentModel model) {
        Stack stack = stackLocal.get();
        if (Objects.isNull(stack)) {
            stack = new Stack();
        }
        stack.push(model);
        stackLocal.set(stack);

        List<TraceSegmentModel> modelList = listLocal.get();
        if (modelList == null) {
            modelList = Lists.newArrayList();
        }
        modelList.add(model);
        listLocal.set(modelList);
    }

    public static void clear() {
        stackLocal.get().clear();
    }

    public static TraceSegment buildTraceSegment() {
        TraceSegment root = doBuildTraceSegment();
        System.out.println("buildTraceSegment >>>: " + JSON.toJSONString(root));
        return root;
    }

    static TraceSegment doBuildTraceSegmentList() {
        List<TraceSegmentModel> segmentModels = listLocal.get();

        Map<Integer, List<TraceSegmentModel>> listMap = segmentModels.stream().collect(Collectors.groupingBy(TraceSegmentModel::getDepth));
        List<Integer> depthList = listMap.keySet().stream().sorted().collect(Collectors.toList());


        TraceSegment root = new TraceSegment();
        root.setMethodName("Service Invocation Monitor | Result ");
        TraceSegment parent = root;

        Set<String> methodSet = new HashSet<>();
        // 相同方法的个数
        Map<String, Integer> methodCountMap = new HashMap<>();
        Map<String, TraceSegment> methodTraceMap = new HashMap<>();
        for (Integer key : depthList) {
            List<TraceSegmentModel> modelList = listMap.get(key);
            if (CollectionUtil.isNotEmpty(modelList)) {
                for (int i = 0, size = modelList.size(); i < size; i++) {
                    TraceSegmentModel enterModel = modelList.get(i);
                    TraceSegmentModel exitModel = modelList.get(size - 1 - i);

                    if (!methodSet.add(enterModel.getMethodName())) {
                        Integer oldCount = methodCountMap.get(enterModel.getMethodName());
                        if (oldCount == null) {
                            oldCount = 1;
                        }
                        methodCountMap.put(enterModel.getMethodName(), oldCount++);
                    }
                    TraceSegment segment = new TraceSegment();
                    segment.setMethodName(enterModel.getMethodName());

                }


            }
            methodSet.clear();

        }
        return null;
    }

    static TraceSegment doBuildTraceSegment() {
        Stack<TraceSegmentModel> stack = stackLocal.get();
        System.out.println("doBuildTraceSegment >>>: " + JSON.toJSONString(stack));
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
                System.out.println(String.format("doBuildTraceSegment.enter >>>: %s,last: %s,current: %s", topModel.getMethodName(), lastExitDepth, topModel.getDepth()));
                while (lastExitDepth != topModel.getDepth()-1) {
                    if (parent != null && parent.getParent()!=null) {
                        parent = parent.getParent();
                        System.out.println("doBuildTraceSegment.enter.while: "+JSON.toJSONString(parent));
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
