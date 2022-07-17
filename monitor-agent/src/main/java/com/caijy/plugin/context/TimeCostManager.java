package com.caijy.plugin.context;

import com.alibaba.fastjson.JSON;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author liguang
 * @date 2022/7/7 星期四 3:14 下午
 */
public class TimeCostManager {

    private static final ConcurrentHashMap<String, Long> serviceTimeCostMap = new ConcurrentHashMap<>();

    private static final CopyOnWriteArrayList<String> sortedService = new CopyOnWriteArrayList();

    public static void start(String k) {
        serviceTimeCostMap.put(k, System.currentTimeMillis());
        sortedService.add(k);
    }

    public static void stop(String k) {
        long timeCost = System.currentTimeMillis() - serviceTimeCostMap.get(k);
        serviceTimeCostMap.put(k, timeCost);
    }

    /**
     * @return |  表头   | 表头  |
     * |  ----  | ----  |
     * | 单元格  | 单元格 |
     * | 单元格  | 单元格 |
     * @author liguang
     **/
    public static void summary() {
        StringBuilder builder = new StringBuilder();
        builder.append(
                "serialNumber |  --------------------------Service-----------------------   |  --timeCost-- |\n");
        builder.append(
                "------------ |  --------------------------------------------------------   |  ------------ |\n");
        for (int i = 0, size = sortedService.size(); i < size; i++) {
            String serviceName = sortedService.get(i);
            long serviceTimeCost = serviceTimeCostMap.get(serviceName);
            String realServiceName = serviceName.substring(0, serviceName.indexOf("$$EnhancerBySpringCGLIB")) + serviceName.substring(serviceName.lastIndexOf("."));
            builder.append(
                    String.format("     NO.%s    |  【%s】   | 【%s】ms     |\n", i + 1, realServiceName, serviceTimeCost));
        }
        serviceTimeCostMap.clear();
        sortedService.clear();
        System.out.println(builder);
    }
}
