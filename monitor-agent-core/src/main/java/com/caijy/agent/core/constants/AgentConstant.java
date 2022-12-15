package com.caijy.agent.core.constants;

/**
 * @author liguang
 * @date 2022/8/8 星期一 8:43 下午
 */
public interface AgentConstant {
    /**
     * 监控的项目编号
     **/
    String MONITOR_PROJECT_CODE = "monitor.project.code";

    /**
     * agent包路径
     **/
    String MONITOR_AGENT_PATH = "monitor.agent.path";

    /**
     * 监控的包名
     **/
    String MONITOR_PACKAGE = "monitor.package";

    /**
     * 判断高耗时的阀值，单位毫秒
     **/
    String MONITOR_TIME_COST_THRESHOLD = "monitor.timeCost.threshold";

    /**
     * 采样率，精度为1/10000. 10000 means 100% sample in default.
     * The sample rate precision is 1/10000. 10000 means 100% sample in default.
     **/
    String MONITOR_SAMPLE_RATE = "monitor.sampleRate";

    /**
     * 输出到控制台的字体颜色
     **/
    String CONSOLE_COLOR_GREEN = "GREEN";
    /**
     * 输出到控制台的字体颜色
     **/
    String CONSOLE_COLOR_RED = "RED";
    /**
     * 输出到控制台的字体颜色
     **/
    String CONSOLE_COLOR_CYAN = "CYAN";
}
