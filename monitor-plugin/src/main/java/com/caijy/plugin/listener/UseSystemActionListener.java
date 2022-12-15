package com.caijy.plugin.listener;

import java.awt.event.ActionEvent;

import javax.swing.*;

import com.caijy.agent.core.command.Commandinitializer;
import com.caijy.plugin.config.ToolsConfiguration;

/**
 * @author liguang
 * @date 2022/10/12 星期三 8:14 下午
 */
public class UseSystemActionListener extends AbstractAction {

    private final JTextField textField;

    private final ToolsConfiguration toolsConfiguration;

    public UseSystemActionListener(JTextField textField, ToolsConfiguration toolsConfiguration) {
        this.textField = textField;
        this.toolsConfiguration = toolsConfiguration;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textField.setText(String.valueOf(Commandinitializer.DEFAULT_VALUE_TIME_COST_THRESHOLD));
        toolsConfiguration.apply();
    }
}
