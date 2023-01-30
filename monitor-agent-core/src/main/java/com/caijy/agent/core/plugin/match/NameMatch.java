package com.caijy.agent.core.plugin.match;

/**
 * @author liguang
 * @date 2022/12/30 星期五 3:11 下午
 */
public class NameMatch implements ClassMatch{

    private String className;

    private NameMatch(String className){
        this.className=className;
    }

    public String getClassName(){return this.className;}

    public static NameMatch byName(String className){
        return new NameMatch(className);
    }
}
