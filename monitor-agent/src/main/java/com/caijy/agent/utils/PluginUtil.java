package com.caijy.agent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.caijy.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.caijy.agent.core.plugin.PluginDefine;
import com.caijy.agent.core.plugin.loader.AgentClassLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liguang
 * @date 2022/12/30 星期五 4:02 下午
 */
@Slf4j
public class PluginUtil {

    public static List<AbstractClassEnhancePluginDefine> loadPlugin() {
        List<PluginDefine> pluginClassList = new LinkedList<>();
        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<>();
        try {
            List<URL> resources = getResources();
            if (CollectionUtil.isNotEmpty(resources)) {
                for (URL url : resources) {
                    load(url.openStream(), pluginClassList);
                }
            }
            for (PluginDefine pluginDefine : pluginClassList) {
                AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine)Class.forName(
                    pluginDefine.getDefineClass(), true, AgentClassLoader
                        .getDefault()).newInstance();
                plugins.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("load plugin failure.");
        }

        return plugins;
    }

    public static List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<URL>();
        Enumeration<URL> urls;
        try {
            urls = AgentClassLoader.getDefault().getResources("plugin.def");

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
//                log.debug("find plugin define in {}", pluginUrl);
            }

            return cfgUrlPaths;
        } catch (Exception e) {
            System.err.println("read resources failure.");
        }
        return null;
    }

    public static void load(InputStream input, List<PluginDefine> pluginClassList) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String pluginDefine;
            while ((pluginDefine = reader.readLine()) != null) {
                try {
                    if (pluginDefine.trim().length() == 0 || pluginDefine.startsWith("#")) {
                        continue;
                    }
                    PluginDefine plugin = PluginDefine.build(pluginDefine);
                    pluginClassList.add(plugin);
                } catch (IllegalStateException e) {
                    System.err.println("Failed to format plugin(" + pluginDefine + ") define.");
                }
            }
        } finally {
            input.close();
        }
    }

}
