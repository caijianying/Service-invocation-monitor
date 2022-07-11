package com.caijy.plugin.utils;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.caijy.plugin.constants.PluginAgentConstants;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

public class PluginUtil {

    private static final IdeaPluginDescriptor IDEA_PLUGIN_DESCRIPTOR;

    static {
        PluginId pluginId = PluginId.getId(PluginAgentConstants.PLUGIN_ID);
        IDEA_PLUGIN_DESCRIPTOR = PluginManagerCore.getPlugin(pluginId);
    }

    /**
     * 获取核心Jar路径
     *
     * @return String
     */
    public static String getAgentCoreJarPath() {
        return getJarPathByStartWith(PluginAgentConstants.AGENT_NAME);
    }

    /**
     * 根据jar包的前缀名称获路径
     *
     * @param startWith 前缀名称
     * @return String
     */
    private static String getJarPathByStartWith(String startWith) {
        final String quotes = "\"";
        if (Objects.nonNull(IDEA_PLUGIN_DESCRIPTOR.getPath())) {
            //MessageUtil.info("agentLib:" + IDEA_PLUGIN_DESCRIPTOR.getPath());
        } else {
            MessageUtil.warn("agentLib not exist!");
        }
        List<File> files = FileUtil.loopFiles(IDEA_PLUGIN_DESCRIPTOR.getPath());
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(startWith)) {
                String pathStr = FileUtil.getCanonicalPath(file);
                if (StrUtil.contains(pathStr, StrUtil.SPACE)) {
                    return StrUtil.builder().append(quotes).append(pathStr).append(quotes).toString();
                }
                return pathStr;
            }
        }
        return StrUtil.EMPTY;
    }

}