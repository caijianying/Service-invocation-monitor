package com.caijy.plugin.dto;

/**
 * @author liguang
 * @date 2022/12/16 星期五 10:55 上午
 */
public class CommandDTO {

    /**
     * 命令项
     **/
    private String command;

    /**
     * 命令值
     **/
    private String commandValue;

    /**
     * 系统按钮名称
     **/
    private String sysBtnName;


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public void setCommandValue(String commandValue) {
        this.commandValue = commandValue;
    }

    public String getSysBtnName() {
        return sysBtnName;
    }

    public void setSysBtnName(String sysBtnName) {
        this.sysBtnName = sysBtnName;
    }
}
