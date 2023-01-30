package com.caijy.agent.core.plugin.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.caijy.agent.core.boot.AgentPkgNotFoundException;
import com.caijy.agent.core.boot.AgentPkgPath;
import com.caijy.agent.core.boot.PluginBootstrap;
import com.caijy.agent.core.log.LogFactory;
import com.caijy.agent.core.log.Logger;
import lombok.RequiredArgsConstructor;

/**
 * @author liguang
 * @date 2022/12/19 星期一 9:49 上午
 */
public class AgentClassLoader extends ClassLoader {

    Logger log = LogFactory.getLogger(AgentClassLoader.class);

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

        File[] files = agentDictionary.listFiles();
        for (File file : Arrays.asList(files)) {
            if (file.exists() && file.isDirectory() && file.getName().equals("plugins")) {
                classpath.add(file);
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry == null) {
                continue;
            }
            try {
                URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                byte[] data;
                try (final BufferedInputStream is = new BufferedInputStream(
                    classFileUrl.openStream()); final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int ch;
                    while ((ch = is.read()) != -1) {
                        baos.write(ch);
                    }
                    data = baos.toByteArray();
                }
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public Enumeration<URL> getResources(String name) {
        List<URL> allResources = new LinkedList<>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = doGetJars();
                }
            } finally {
                jarScanLock.unlock();
            }
        }

        return allJars;
    }

    private LinkedList<Jar> doGetJars() {
        LinkedList<Jar> jars = new LinkedList<>();
        for (File path : classpath) {
            if (path.exists() && path.isDirectory()) {
                String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
                for (String fileName : jarFileNames) {
                    try {
                        File file = new File(path, fileName);
                        Jar jar = new Jar(new JarFile(file), file);
                        jars.add(jar);
                        log.debug("%s loaded.", file.toString());
                    } catch (IOException e) {
                        System.err.println(String.format("%s jar file can't be resolved", fileName));
                    }
                }
            }
        }
        return jars;
    }

    @RequiredArgsConstructor
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;
    }
}
