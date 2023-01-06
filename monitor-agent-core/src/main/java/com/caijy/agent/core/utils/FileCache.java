package com.caijy.agent.core.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.io.FileUtil;
import com.caijy.agent.core.console.TraceConsoleDTO;

/**
 * @author liguang
 * @date 2022/12/9 星期五 3:58 下午
 */
public class FileCache {
    /**
     * 往日志文件追加trace日志
     *
     * @param agentJarPath:
     * @param projectCode:
     * @param consoleDTOList:
     * @return
     * @author liguang
     * @date 2022/12/15 3:38 下午
     **/
    public static void appendLines(String agentJarPath, String projectCode, List<TraceConsoleDTO> consoleDTOList) {
        String logFilePath = getLogFilePath(agentJarPath, projectCode);
        if (logFilePath == null) {
            return;
        }
        List<String> list = consoleDTOList.stream().map(JSON::toJSONString).collect(Collectors.toList());
        FileUtil.appendLines(list, logFilePath, StandardCharsets.UTF_8);
    }

    public static String getLogFilePath(String agentJarPath, String projectCode) {
        File agentJar = new File(agentJarPath);
        if (agentJar.exists()) {
            String projectDir = agentJar.getParentFile().getParentFile().getAbsolutePath();
            return String.format("%s/logs/%s.log", projectDir, projectCode);
        }
        return null;
    }

}
