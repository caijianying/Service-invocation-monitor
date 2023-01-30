package com.caijy.agent.core.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author liguang
 * @date 2022/12/19 星期一 10:02 上午
 */
public class AgentPkgPath {

    private static File AGENT_PKG_PATH;

    public static File getPath() throws AgentPkgNotFoundException {
        if (AGENT_PKG_PATH == null) {
            AGENT_PKG_PATH = findPath();
        }
        return AGENT_PKG_PATH;
    }

    public static boolean isPathFound() {
        return AGENT_PKG_PATH != null;
    }

    private static File findPath() throws AgentPkgNotFoundException {
        String classResourcePath = AgentPkgPath.class.getName().replaceAll("\\.", "/") + ".class";

        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();
            System.out.println(String.format("The beacon class location is %s.",urlString));

            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    System.err.println(String.format("Can not locate agent jar file by url: %s.",urlString));
                }
                if (agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                    prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }

        System.err.println("Can not locate agent jar file.");
        throw new AgentPkgNotFoundException("Can not locate agent jar file.");
    }
}
