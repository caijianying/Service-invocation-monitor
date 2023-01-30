package com.caijy.plugin.filter;

import com.intellij.execution.filters.Filter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author liguang
 * @date 2022/12/12 星期一 7:46 下午
 */
public class ConsoleFilter implements Filter {
    static String m = "\u001B[95m\n"
        + " __  _____   _   ___    ___   _   ___    ___   _   ___ \n"
        + " \\ \\/ /_ _| /_\\ / _ \\  | _ ) /_\\ |_ _|  / __| /_\\ |_ _|\n"
        + "  >  < | | / _ \\ (_) | | _ \\/ _ \\ | |  | (__ / _ \\ | | \n"
        + " /_/\\_\\___/_/ \\_\\___/  |___/_/ \\_\\___|  \\___/_/ \\_\\___|\n"
        + "                                                       \n"
        + "\u001B[32m\n"
        + "*********************************************************  \n"
        + "ENV_CODE     : dev \n"
        + "Version      : 1.0.0 \n"
        + "ConfigStore  : name1 \n"
        + "ConfigServer : 127.0.0.1 \n"
        + "SSL          : xxxx   \n"
        + "*********************************************************\u001B[2;0;39m";
    @Override
    public @Nullable Result applyFilter(@NotNull String s, int i) {

        return null;
    }

    public static void main(String[] args) {
        System.out.println(m);
    }
}
