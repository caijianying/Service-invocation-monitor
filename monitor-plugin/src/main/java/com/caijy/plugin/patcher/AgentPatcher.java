package com.caijy.plugin.patcher;

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

    @Override
    public void patchJavaParameters(Executor executor, RunProfile runProfile, JavaParameters javaParameters) {
        RunConfiguration runConfiguration = (RunConfiguration)runProfile;
        ParametersList vmParametersList = javaParameters.getVMParametersList();

        // 包名优先从命令获取
        String packageName = null;
        String packageParam = vmParametersList.getParameters().stream().filter(
            t -> t.indexOf("-Dmonitor.package=") != -1).findFirst().orElse(null);
        if (StringUtils.isNotBlank(packageParam)) {
            packageName = packageParam.substring(packageParam.indexOf("=") + 1);
        }

        if (StringUtils.isBlank(packageName)) {
            // 默认使用自动类的包名
            packageName = this.getMainClassPackageName(javaParameters.getMainClass());
        }

        if (StringUtils.isBlank(packageName)) {
            MessageUtil.warn("vm options is missing, you can add -Dmonitor.package to enable Link Monitoring! ");
            return;
        }
        String agentCoreJarPath = PluginUtil.getAgentCoreJarPath();
        if (StringUtils.isBlank(agentCoreJarPath)) {
            MessageUtil.warn("cannot load agent successfully !");
            return;
        }
        vmParametersList.addParametersString(
            "-javaagent:" + agentCoreJarPath + "=" + packageName);
        vmParametersList.addNotEmptyProperty("service-invocation-monitor.projectId",
            runConfiguration.getProject().getLocationHash());
    }

    private String getMainClassPackageName(String mainClass ){
        if (StringUtils.isNotBlank(mainClass)){
            return mainClass.substring(0,mainClass.lastIndexOf("."));
        }
        return null;
    }
}
