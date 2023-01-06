package com.caijy.plugin.listener;

import java.util.List;

import com.caijy.plugin.ui.MonitorOutputToolWindow;
import com.caijy.plugin.utils.PluginUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author liguang
 * @date 2023/1/5 星期四 3:34 下午
 */
public class MonitorOutPutToolWindowListener implements ToolWindowManagerListener {

    private final Project project;

    public MonitorOutPutToolWindowListener(Project project) {this.project = project;}

    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        System.out.println(String
            .format("toolWindowsRegistered,ids=%s,toolWindowManager:%s", ids, toolWindowManager.getToolWindowIds()));
    }

    @Override
    public void toolWindowUnregistered(@NotNull String id, @NotNull ToolWindow toolWindow) {
        System.out.println(String
            .format("toolWindowUnregistered,ids=%s,toolWindowManager:%s", id, toolWindow.getId()));
    }

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        System.out.println(String
            .format("toolWindowShown,toolWindowManager:%s", toolWindow.getId()));
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        if (MonitorOutputToolWindow.ID.equals(toolWindowManager.getActiveToolWindowId())) {
            PluginUtil.refreshConsoleLog(project);
        }
    }
}
