package com.caijy.agent.core.console;

/**
 * @author liguang
 * @date 2022/12/15 星期四 10:38 上午
 */
public class TraceConsoleDTO {

    private String color;

    private String text;

    public TraceConsoleDTO(String color, String text) {
        this.color = color;
        this.text = text;
    }

    public TraceConsoleDTO() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TraceConsoleDTO{" +
            "color='" + color + '\'' +
            ", text='" + text + '\'' +
            '}';
    }
}
