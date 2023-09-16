package io.github.srdjanv.localgitdependency.logger;

import static io.github.srdjanv.localgitdependency.logger.PluginLogger.*;

import io.github.srdjanv.localgitdependency.Constants;

public class ManagerLogger {
    private ManagerLogger() {}

    public static void info(String log, Object... args) {
        logger.lifecycle(Constants.TAB_INDENTX2 + ANSI_GREEN + log + ANSI_RESET, args);
    }

    public static void error(String log, Object... objects) {
        logger.error(Constants.TAB_INDENTX2 + ANSI_RED + log + ANSI_RESET, objects);
    }

    public static void warn(String log, Object... args) {
        logger.warn(Constants.TAB_INDENTX2 + ANSI_YELLOW + log + ANSI_RESET, args);
    }

    public static void warn(String log, Throwable t) {
        logger.warn(Constants.TAB_INDENTX2 + ANSI_YELLOW + log + ANSI_RESET, t);
    }
}
