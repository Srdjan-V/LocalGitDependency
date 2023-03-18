package io.github.srdjanv.localgitdependency.logger;

import io.github.srdjanv.localgitdependency.Constants;

public class ManagerLogger {
    private ManagerLogger() {
    }

    public static void infoUnFormatted(String info, Object... args) {
        PluginLogger.logger.lifecycle(info, args);
    }

    public static void info(String info, Object... args) {
        PluginLogger.logger.lifecycle(Constants.TAB_INDENTX2 + info, args);
    }

    public static void error(String error, Object... args) {
        PluginLogger.logger.error(Constants.TAB_INDENTX2 + error, args);
    }

    public static void warn(String warn, Object... args) {
        PluginLogger.logger.warn(Constants.TAB_INDENTX2 + warn, args);
    }
}
