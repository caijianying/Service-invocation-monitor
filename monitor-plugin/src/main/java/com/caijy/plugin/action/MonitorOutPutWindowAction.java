package com.caijy.plugin.action;

import com.caijy.plugin.utils.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author liguang
 * @date 2023/1/5 星期四 4:18 下午
 */
public class MonitorOutPutWindowAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PluginUtil.showAllConsoleLog(anActionEvent.getProject());
    }
}
