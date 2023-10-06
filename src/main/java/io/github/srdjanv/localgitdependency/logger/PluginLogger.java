package io.github.srdjanv.localgitdependency.logger;

import io.github.srdjanv.localgitdependency.Constants;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class PluginLogger {
    PluginLogger() {}

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_CYAN = "\u001B[36m";
    static final String ANSI_RED = "\u001B[31m";

    public static final Logger logger = Logging.getLogger("local-git-dependency");

    public static void title(String info, Object... args) {
        logger.lifecycle(decorateCyan(info), args);
    }

    public static void task(String info, Object... args) {
        logger.lifecycle(Constants.TAB_INDENT + decorateCyan(info), args);
    }

    public static void warn(String log, Object... args) {
        logger.warn(decorateYellow(log), args);
    }

    public static void warn(String log, Throwable t) {
        logger.warn(decorateYellow(log), t);
    }

    public static void error(String log, Object... args) {
        logger.error(decorateRed(log), args);
    }

    public static void error(String log, Throwable e) {
        logger.error(decorateRed(log), e);
    }

    public static String decorateCyan(String info) {
        return ANSI_CYAN + info + ANSI_RESET;
    }

    public static String decorateGreen(String info) {
        return ANSI_GREEN + info + ANSI_RESET;
    }

    public static String decorateYellow(String warn) {
        return ANSI_YELLOW + warn + ANSI_RESET;
    }

    public static String decorateRed(String error) {
        return ANSI_RED + error + ANSI_RESET;
    }
}
