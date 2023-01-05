package com.caijy.plugin.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.caijy.plugin.utils.PluginUtil;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

/**
 * @author liguang
 * @date 2022/12/7 星期三 8:18 下午
 */
public class MonitorOutputToolWindow implements ToolWindowFactory {
    private static final Map<Project, ConsoleView> consoleViews = new HashMap<>();

    private static final ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);

    public static ToolWindow toolWindow;

    public static final String ID = "Monitor";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        if (consoleViews.get(project) == null) {
            createToolWindow(project, toolWindow);
            //initLogFileReadTask(project);
        }
        MonitorOutputToolWindow.toolWindow = toolWindow;
        MonitorOutputToolWindow.toolWindow.activate(null, false);
    }

    private void initLogFileReadTask(Project project) {
        schedule.scheduleAtFixedRate(() -> PluginUtil.refreshConsoleLog(project), 0, 1, TimeUnit.SECONDS);
    }

    // 为了方便其他地方使用 ConsoleView ，定义该方法获取 ConsoleView
    // ConsoleView 对象通过其 print 方法，向自定义的控制台窗口输出文本内容
    public static ConsoleView getConsoleView(Project project) {
        // 创建过了就不再重复创建了
        if (consoleViews.get(project) == null) {
            ToolWindow toolWindow = getToolWindow(project);
            createToolWindow(project, toolWindow);
        }
        return consoleViews.get(project);
    }

    // 设置工具栏窗口显示的 UI 为控制台窗口视图
    private static void createToolWindow(Project project, ToolWindow toolWindow) {
        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ConsoleView consoleView = builder.getConsole();
        consoleViews.put(project, consoleView);
        Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(),
            "Monitor Console Output", false);
        toolWindow.getContentManager().addContent(content);
        content.getComponent().setVisible(true);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }

    public static ToolWindow getToolWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(ID);
    }
}
