package com.caijy.plugin.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.caijy.agent.core.console.TraceConsoleDTO;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.agent.core.utils.FileCache;
import com.caijy.plugin.constants.PluginAgentConstant;
import com.caijy.plugin.ui.MonitorOutputToolWindow;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;

public class PluginUtil {

    private static Map<String, Integer> lastLogIndexCache = new HashMap<>();
    private static final IdeaPluginDescriptor IDEA_PLUGIN_DESCRIPTOR;

    static {
        PluginId pluginId = PluginId.getId(PluginAgentConstant.PLUGIN_ID);
        IDEA_PLUGIN_DESCRIPTOR = PluginManagerCore.getPlugin(pluginId);
    }

    /**
     * 获取核心Jar路径
     *
     * @return String
     */
    public static String getAgentCoreJarPath() {
        return getJarPath(PluginAgentConstant.AGENT_PREFIX, PluginAgentConstant.AGENT_SUFFIX);
    }

    /**
     * 根据jar包的前缀名称获路径
     *
     * @param startWith 前缀名称
     * @return String
     */
    private static String getJarPath(String startWith, String suffix) {
        final String quotes = "\"";
        if (Objects.nonNull(IDEA_PLUGIN_DESCRIPTOR.getPath())) {
            //MessageUtil.info("agentLib:" + IDEA_PLUGIN_DESCRIPTOR.getPath());
        } else {
            MessageUtil.warn("agentLib not exist!");
        }
        List<File> files = FileUtil.loopFiles(IDEA_PLUGIN_DESCRIPTOR.getPath());
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(startWith) && name.endsWith(suffix)) {
                String pathStr = FileUtil.getCanonicalPath(file);
                if (StrUtil.contains(pathStr, StrUtil.SPACE)) {
                    return StrUtil.builder().append(quotes).append(pathStr).append(quotes).toString();
                }
                return pathStr;
            }
        }
        return StrUtil.EMPTY;
    }

    /**
     * 刷新控制台traceLog
     *
     * @param project:
     * @return
     * @author liguang
     * @date 2022/12/15 3:35 下午
     **/
    public static void refreshConsoleLog(Project project) {
        String projectName = project.getName();
        String logFilePath = FileCache.getLogFilePath(getAgentCoreJarPath(), projectName);
        if (logFilePath == null || !new File(logFilePath).exists()) {
            return;
        }
        List<String> list = FileUtil.readLines(logFilePath, StandardCharsets.UTF_8);
        ConsoleView consoleView = MonitorOutputToolWindow.getConsoleView(project);
        Integer lastIndex = lastLogIndexCache.get(projectName);
        lastIndex = lastIndex == null ? -1 : lastIndex;
        int size = list.size();
        for (int i = lastIndex + 1; i < size; i++) {
            printConsole(consoleView, JSON.parseObject(list.get(i), TraceConsoleDTO.class));
        }
        lastLogIndexCache.put(project.getName(), size - 1);
    }

    /**
     * 控制台打印trace内容
     *
     * @param consoleView:
     * @param consoleDTO:
     * @return
     * @author caijy
     * @date 2022/12/15 3:28 下午
     **/
    private static void printConsole(ConsoleView consoleView, TraceConsoleDTO consoleDTO) {
        String showText = consoleDTO.getText();
        TextAttributes textAttributes = ConsoleViewContentType.NORMAL_OUTPUT.getAttributes();
        String color = consoleDTO.getColor();
        if (color == null) {
            TextAttributes darkGrayAttr = new TextAttributes(JBColor.DARK_GRAY, textAttributes.getBackgroundColor(),
                textAttributes.getEffectColor(), textAttributes.getEffectType(), textAttributes.getFontType());
            consoleView.print(showText,
                new ConsoleViewContentType("dark_gray",
                    TextAttributesKey.createTextAttributesKey("DARK_GRAY", darkGrayAttr)));
        }

        if (AgentConstant.CONSOLE_COLOR_CYAN.equals(color)) {
            TextAttributes cyanAttr = new TextAttributes(JBColor.CYAN, textAttributes.getBackgroundColor(),
                textAttributes.getEffectColor(), textAttributes.getEffectType(), textAttributes.getFontType());
            consoleView.print(showText,
                new ConsoleViewContentType("cyan", TextAttributesKey.createTextAttributesKey("CYAN", cyanAttr)));
        }

        if (AgentConstant.CONSOLE_COLOR_GREEN.equals(color)) {
            TextAttributes greenAttr = new TextAttributes(JBColor.GREEN, textAttributes.getBackgroundColor(),
                textAttributes.getEffectColor(), textAttributes.getEffectType(), textAttributes.getFontType());
            consoleView.print(showText,
                new ConsoleViewContentType("green", TextAttributesKey.createTextAttributesKey("GREEN", greenAttr)));
        }

        if (AgentConstant.CONSOLE_COLOR_RED.equals(color)) {
            TextAttributes redAttr = new TextAttributes(JBColor.RED, textAttributes.getBackgroundColor(),
                textAttributes.getEffectColor(), textAttributes.getEffectType(), textAttributes.getFontType());
            consoleView.print(showText,
                new ConsoleViewContentType("red", TextAttributesKey.createTextAttributesKey("RED", redAttr)));
        }
    }

}