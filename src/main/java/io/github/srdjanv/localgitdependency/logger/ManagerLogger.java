package io.github.srdjanv.localgitdependency.logger;

import io.github.srdjanv.localgitdependency.Constants;

public class ManagerLogger extends PluginLogger {
    private ManagerLogger() {}

    public static void info(String log, Object... args) {
        logger.lifecycle(Constants.TAB_INDENTX2 + decorateGreen(log), args);
    }

    public static void error(String log, Object... objects) {
        logger.error(Constants.TAB_INDENTX2 + decorateRed(log), objects);
    }

    public static void warn(String log, Object... args) {
        logger.warn(Constants.TAB_INDENTX2 + decorateYellow(log), args);
    }

    public static void warn(String log, Throwable t) {
        logger.warn(Constants.TAB_INDENTX2 + decorateYellow(log), t);
    }
}
