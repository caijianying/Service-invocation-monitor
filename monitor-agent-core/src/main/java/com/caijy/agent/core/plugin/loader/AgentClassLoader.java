package com.caijy.agent.core.plugin.loader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarFile;

import com.caijy.agent.core.boot.AgentPkgNotFoundException;
import com.caijy.agent.core.boot.AgentPkgPath;
import com.caijy.agent.core.boot.PluginBootstrap;
import lombok.RequiredArgsConstructor;

/**
 * @author liguang
 * @date 2022/12/19 星期一 9:49 上午
 */
public class AgentClassLoader extends ClassLoader{

    static {
        // 为了解决classloader死锁
        registerAsParallelCapable();
    }

    /**
     * The default class loader for the agent.
     */
    private static AgentClassLoader DEFAULT_LOADER;

    private List<File> classpath;
    private List<Jar> allJars;
    private ReentrantLock jarScanLock = new ReentrantLock();

    public static AgentClassLoader getDefault() {
        return DEFAULT_LOADER;
    }

    /**
     * Init the default class loader.
     *
     * @throws AgentPkgNotFoundException if agent package is not found.
     */
    public static void initDefaultLoader() throws AgentPkgNotFoundException {
        if (DEFAULT_LOADER == null) {
            synchronized (AgentClassLoader.class) {
                if (DEFAULT_LOADER == null) {
                    DEFAULT_LOADER = new AgentClassLoader(PluginBootstrap.class.getClassLoader());
                }
            }
        }
    }

    public AgentClassLoader(ClassLoader parent) throws AgentPkgNotFoundException {
        super(parent);
        File agentDictionary = AgentPkgPath.getPath();
        classpath = new LinkedList<>();
        //Config.Plugin.MOUNT.forEach(mountFolder -> classpath.add(new File(agentDictionary, mountFolder)));
    }


    @RequiredArgsConstructor
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;
    }
}
