package com.caijy.plugin.config;

import com.caijy.agent.core.command.Commandinitializer;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.plugin.constants.PluginAgentConstants;
import com.caijy.plugin.listener.UseSystemActionListener;
import com.google.common.collect.Lists;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.JBColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.util.List;

import com.intellij.openapi.util.NlsContexts.ConfigurableName;

/**
 * @author liguang
 * @date 2022/10/11 星期二 5:37 下午
 */
public class ToolsConfiguration implements Configurable {

    List<JTextField> textFieldList = Lists.newArrayList();

    private static final int column = 3;

    private JPanel jPanel;
    private JLabel jLabel;
    private JButton commandBtn;

    public ToolsConfiguration() {
        int row = 18;
        jPanel = new JPanel(new GridLayout(row, column));

        Commandinitializer.DEFAULT_COMMANDS_KV.entrySet().stream().forEach(entry -> {
            // 设置命令行
            JLabel command = new JLabel(entry.getKey() + "：");
            // 优先拿缓存的值
            String costThreshold = ToolsSetting.getInstance().timeCostThreshold;
            // 设置回显
            JTextField textField = new JTextField();
            textField.setText(StringUtils.isNotBlank(costThreshold) ? costThreshold : entry.getValue());
            // 焦点监听
            textField.addFocusListener(
                new TextFieldListener(textField, entry.getValue()));
            // 设置按钮名字
            JButton systemBtn = new JButton("Use System");
            // 设置Action
            systemBtn.addActionListener(new UseSystemActionListener(textField,this));

            jPanel.add(command);
            jPanel.add(textField);
            jPanel.add(systemBtn);
            // 保存输入框
            textFieldList.add(textField);
        });
        // 计算缺省行
        int restRow = (row - Commandinitializer.DEFAULT_COMMANDS.size()) * column;
        for (int i = 0; i < restRow; i++) {
            jPanel.add(new JLabel());
        }
    }

    @Override
    public @ConfigurableName String getDisplayName() {
        return PluginAgentConstants.PLUGIN_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return jPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        //TranslatorToolsWindow.addNote("XX", timeCostThresholdInput.getText());
        for (int i = 0; i < textFieldList.size(); i++) {
            JTextField textField = textFieldList.get(i);
            Commandinitializer.DEFAULT_COMMANDS_KV.put(Commandinitializer.DEFAULT_COMMANDS.get(i), textField.getText());
        }

        String commandValue = Commandinitializer.DEFAULT_COMMANDS_KV.get(
            Commandinitializer.formatCommand(AgentConstant.MONITOR_TIME_COST_THRESHOLD));
        ToolsSetting.getInstance().timeCostThreshold = commandValue;
    }

    public JPanel getjPanel() {
        return jPanel;
    }

    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }

    public JLabel getjLabel() {
        return jLabel;
    }

    public void setjLabel(JLabel jLabel) {
        this.jLabel = jLabel;
    }

    public JButton getCommandBtn() {
        return commandBtn;
    }

    public void setCommandBtn(JButton commandBtn) {
        this.commandBtn = commandBtn;
    }

    static class TextFieldListener implements FocusListener {

        private final String defaultHint;
        private final JTextField textField;

        public TextFieldListener(JTextField textField, String defaultHint) {
            this.defaultHint = defaultHint;
            this.textField = textField;
        }

        @Override
        public void focusGained(FocusEvent e) {
            // 清空提示语，设置为黑色字体
            if (textField.getText().equals(defaultHint)) {
                textField.setText("");
                textField.setForeground(JBColor.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            // 如果内容为空，设置提示语
            if (StringUtils.isBlank(textField.getText())) {
                textField.setText(defaultHint);
                textField.setForeground(JBColor.GRAY);
            }
        }
    }
}
