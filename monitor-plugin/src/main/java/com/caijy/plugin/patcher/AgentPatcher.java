package com.caijy.plugin.patcher;

import com.caijy.agent.core.command.Commandinitializer;
import com.caijy.agent.core.constants.AgentConstant;
import com.caijy.plugin.config.ToolsSetting;
import com.caijy.plugin.utils.MessageUtil;
import com.caijy.plugin.utils.PluginUtil;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liguang
 * @date 2022/7/6 星期三 10:36 上午
 */
public class AgentPatcher extends JavaProgramPatcher {

    /**
     * 监控的项目编号
     **/
    final String MONITOR_PROJECT_CODE = Commandinitializer.formatCommand(AgentConstant.MONITOR_PROJECT_CODE);

    /**
     * agent包路径
     **/
    final String MONITOR_AGENT_PATH = Commandinitializer.formatCommand(AgentConstant.MONITOR_AGENT_PATH);

    /**
     * 监控的包名
     **/
    final String MONITOR_PACKAGE = Commandinitializer.formatCommand(AgentConstant.MONITOR_PACKAGE);

    /**
     * 高耗时阀值
     **/
    final String TIME_COST_THRESHOLD = Commandinitializer.formatCommand(AgentConstant.MONITOR_TIME_COST_THRESHOLD);

    /**
     * 采样率
     **/
    final String MONITOR_SAMPLE_RATE = Commandinitializer.formatCommand(AgentConstant.MONITOR_SAMPLE_RATE);

    /**
     * MAVEN底层实现类的包名
     **/
    String MAVEN_PACKAGE = "org.codehaus";

    /**
     * SpringBootTest测试类底层实现类的包名
     **/
    String SPRING_BOOT_TEST_PACKAGE = "com.intellij";

    @Override
    public void patchJavaParameters(Executor executor, RunProfile runProfile, JavaParameters javaParameters) {
        RunConfiguration runConfiguration = (RunConfiguration)runProfile;
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        String mainClass = javaParameters.getMainClass();

        // 主要针对Maven底层实现类的过滤
        if (StringUtils.isNotBlank(mainClass) && mainClass.startsWith(MAVEN_PACKAGE)) {
            return;
        }
        // 包名优先从命令获取
        String packageName = null;
        String packageParam = vmParametersList.getParameters().stream().filter(
            t -> t.indexOf(MONITOR_PACKAGE) != -1).findFirst().orElse(null);
        if (StringUtils.isNotBlank(packageParam)) {
            int packageValueIndex = packageParam.indexOf("=");
            packageName = packageValueIndex == -1 ? null : packageParam.substring(packageValueIndex + 1);
        }

        if (StringUtils.isBlank(packageName) && !mainClass.startsWith(SPRING_BOOT_TEST_PACKAGE)) {
            // 默认使用启动类的包名
            packageName = this.getMainClassPackageName(mainClass);
        }

        String agentCoreJarPath = PluginUtil.getAgentCoreJarPath();
        if (StringUtils.isBlank(agentCoreJarPath)) {
            MessageUtil.warn("cannot load agent successfully !");
            return;
        }

        if (StringUtils.isNotBlank(packageName)) {
            vmParametersList.replaceOrAppend(MONITOR_PACKAGE,
                String.format("%s=%s", MONITOR_PACKAGE, packageName));
        }

        // 优先拿用户自定义的高耗时阀值
        String threshold = vmParametersList.getParameters().stream().filter(
            t -> t.indexOf(TIME_COST_THRESHOLD) != -1).findFirst().orElse(null);
        if (StringUtils.isBlank(threshold)) {
            threshold = ToolsSetting.getInstance().timeCostThreshold;
            if (StringUtils.isBlank(threshold)) {
                threshold = String.valueOf(Commandinitializer.DEFAULT_VALUE_TIME_COST_THRESHOLD);
                ToolsSetting.getInstance().timeCostThreshold = threshold;
            }
        }else {
            String[] thresholdCommand = threshold.split("=");
            if (thresholdCommand.length == 2){
                threshold = thresholdCommand[1];
            }
        }
        vmParametersList.replaceOrAppend(TIME_COST_THRESHOLD,
            String.format("%s=%s", TIME_COST_THRESHOLD, threshold));

        // 优先拿用户自定义的采样率
        String sampleRate = vmParametersList.getParameters().stream().filter(
            t -> t.indexOf(MONITOR_SAMPLE_RATE) != -1).findFirst().orElse(null);
        if (StringUtils.isBlank(sampleRate)) {
            sampleRate = ToolsSetting.getInstance().sampleRate;
            if (StringUtils.isBlank(sampleRate)) {
                sampleRate = String.valueOf(Commandinitializer.DEFAULT_VALUE_SAMPLE_RATE);
                ToolsSetting.getInstance().sampleRate = sampleRate;
            }
        }else {
            String[] sampleRateCommand = sampleRate.split("=");
            if (sampleRateCommand.length == 2){
                sampleRate = sampleRateCommand[1];
            }
        }
        vmParametersList.replaceOrAppend(MONITOR_SAMPLE_RATE,
            String.format("%s=%s", MONITOR_SAMPLE_RATE, sampleRate));

        // 其他设置
        vmParametersList.addParametersString(
            "-javaagent:" + agentCoreJarPath);
        vmParametersList.addNotEmptyProperty("service-invocation-monitor.projectId",
            runConfiguration.getProject().getLocationHash());
        vmParametersList.addParametersString(
            String.format("%s=%s", MONITOR_PROJECT_CODE, runConfiguration.getProject().getName()));
        vmParametersList.addParametersString(
            String.format("%s=%s", MONITOR_AGENT_PATH, agentCoreJarPath));

    }

    private String getMainClassPackageName(String mainClass) {
        if (StringUtils.isNotBlank(mainClass)) {
            return mainClass.substring(0, mainClass.lastIndexOf("."));
        }
        return null;
    }
}
