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

    /**
     * 监控的包名
     **/
    String MONITOR_PACKAGE = "-Dmonitor.package";

    @Override
    public void patchJavaParameters(Executor executor, RunProfile runProfile, JavaParameters javaParameters) {
        RunConfiguration runConfiguration = (RunConfiguration)runProfile;
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        String mainClass = javaParameters.getMainClass();

        // 主要针对SpringBootTest和Maven底层实现类的过滤
        if (StringUtils.isNotBlank(mainClass) && mainClass.startsWith("org.codehaus")){
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

        if (StringUtils.isBlank(packageName) && !mainClass.startsWith("com.intellij")) {
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
        vmParametersList.addParametersString(
            "-javaagent:" + agentCoreJarPath);
        vmParametersList.addNotEmptyProperty("service-invocation-monitor.projectId",
            runConfiguration.getProject().getLocationHash());
    }

    private String getMainClassPackageName(String mainClass) {
        if (StringUtils.isNotBlank(mainClass)) {
            return mainClass.substring(0, mainClass.lastIndexOf("."));
        }
        return null;
    }
}
