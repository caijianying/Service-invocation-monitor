package com.caijy.agent.core.boot;

import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author caijy
 * @description
 * @date 2024/2/17 星期六 4:53 下午
 */
@Slf4j
public enum ServiceManager {
    INSTANCE;

    private Map<Class, BootService> bootedServices = Collections.emptyMap();

    public void boot() {
        bootedServices = loadAllServices();

        bootedServices.values().stream().forEach(service -> {
            try {
                service.boot();
            } catch (Throwable ex) {
                log.error("ServiceManager try to start [{}] fail.", service.getClass().getName(), ex);
            }
        });
    }

    private Map<Class, BootService> loadAllServices() {
        Map<Class, BootService> bootedServices = new LinkedHashMap<>();
        List<BootService> allServices = new LinkedList<>();
        load(allServices);
        for (BootService service : allServices) {
            bootedServices.put(service.getClass(), service);
        }
        return bootedServices;
    }

    public void shutdown() {
        bootedServices.values().stream().forEach(service -> {
            try {
                service.shutdown();
            } catch (Throwable e) {
                log.error("ServiceManager try to shutdown [{}] fail.", service.getClass().getName(),e);
            }
        });
    }

    void load(List<BootService> allServices) {
        for (final BootService bootService : ServiceLoader.load(BootService.class, AgentClassLoader.getDefault())) {
            allServices.add(bootService);
        }
    }
}
