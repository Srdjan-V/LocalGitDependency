package io.github.srdjanv.localgitdependency.logger;

import io.github.srdjanv.localgitdependency.Constants;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class PluginLogger {
    private PluginLogger() {}

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_CYAN = "\u001B[36m";
    static final String ANSI_RED = "\u001B[31m";

    static final Logger logger = Logging.getLogger("local-git-dependency");

    public static void title(String info, Object... args) {
        logger.lifecycle(ANSI_CYAN + info + ANSI_RESET, args);
    }

    public static void task(String info, Object... args) {
        logger.lifecycle(Constants.TAB_INDENT + ANSI_CYAN + info + ANSI_RESET, args);
    }

    public static void warn(String log, Object... args) {
        logger.warn(ANSI_YELLOW + log + ANSI_RESET, args);
    }

    public static void warn(String log, Throwable t) {
        logger.warn(ANSI_YELLOW + log + ANSI_RESET, t);
    }

    public static void error(String log, Object... args) {
        logger.error(ANSI_RED + log + ANSI_RESET, args);
    }

    public static void error(String log, Throwable e) {
        logger.error(ANSI_RED + log + ANSI_RESET, e);
    }
}
