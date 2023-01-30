package com.caijy.plugin.handler;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author liguang
 * @date 2022/12/12 星期一 7:31 下午
 */
public class ColoredKillableColoredProcessHandler extends KillableColoredProcessHandler {
    public ColoredKillableColoredProcessHandler(
        @NotNull GeneralCommandLine commandLine)
        throws ExecutionException {
        super(commandLine);
        setShouldKillProcessSoftly(true);
    }

    public ColoredKillableColoredProcessHandler(
        @NotNull GeneralCommandLine commandLine, boolean withMediator) throws ExecutionException {
        super(commandLine, withMediator);
        setShouldKillProcessSoftly(true);
    }

    public ColoredKillableColoredProcessHandler(@NotNull Process process, String commandLine) {
        super(process, commandLine);
        setShouldKillProcessSoftly(true);
    }

    public ColoredKillableColoredProcessHandler(@NotNull Process process, String commandLine,
        @NotNull Charset charset) {
        super(process, commandLine, charset);
        setShouldKillProcessSoftly(true);
    }

    public ColoredKillableColoredProcessHandler(@NotNull Process process, String commandLine,
        @NotNull Charset charset, @Nullable Set<? extends File> filesToDelete) {
        super(process, commandLine, charset, filesToDelete);
        setShouldKillProcessSoftly(true);
    }
}
