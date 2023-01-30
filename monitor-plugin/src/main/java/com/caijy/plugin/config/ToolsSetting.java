package com.caijy.plugin.config;

import com.caijy.plugin.constants.PluginAgentConstant;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author liguang
 * @date 2022/10/11 星期二 9:02 下午
 */
@State(name = PluginAgentConstant.PLUGIN_NAME, storages = {@Storage(value = "monitor-tools-setting.xml")})
public class ToolsSetting implements PersistentStateComponent<ToolsSetting> {

    public String timeCostThreshold;
    public String sampleRate;

    public static ToolsSetting getInstance() {
        return ApplicationManager.getApplication().getService(ToolsSetting.class);
    }

    @Override
    public @Nullable ToolsSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ToolsSetting toolsSetting) {
        this.timeCostThreshold = toolsSetting.timeCostThreshold;
    }
}
