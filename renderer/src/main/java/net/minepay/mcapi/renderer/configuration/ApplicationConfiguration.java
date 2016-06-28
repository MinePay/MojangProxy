package net.minepay.mcapi.renderer.configuration;

import com.google.common.base.Preconditions;

import org.apache.commons.cli.CommandLine;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Represents the current application configuration.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ApplicationConfiguration {
    private final boolean debug;
    private final int pauseTime;
    private final int threadCount;
    private final boolean showWindow;

    public ApplicationConfiguration(boolean debug, @Nonnegative int pauseTime, @Nonnegative int threadCount, boolean showWindow) {
        Preconditions.checkArgument(pauseTime > 0, "Pause timer cannot be negative");
        Preconditions.checkArgument(threadCount > 0, "Thread count cannot be negative");

        this.debug = debug;
        this.pauseTime = pauseTime;
        this.threadCount = threadCount;
        this.showWindow = showWindow;
    }

    public ApplicationConfiguration(@Nonnull CommandLine commandLine) {
        this(commandLine.hasOption("debug"), (commandLine.hasOption("wait") ? Integer.parseUnsignedInt(commandLine.getOptionValue("wait")) : 0), (commandLine.hasOption("threads") ? Integer.parseUnsignedInt(commandLine.getOptionValue("threads")) : 0), commandLine.hasOption("window"));
    }

    public boolean isDebug() {
        return this.debug;
    }

    @Nonnegative
    public int getPauseTime() {
        return this.pauseTime;
    }

    @Nonnegative
    public int getThreadCount() {
        return this.threadCount;
    }

    public boolean isShowWindow() {
        return this.showWindow;
    }
}
