package io.github.srdjanv.localgitdependency;

public class Logger {
    private final static org.gradle.api.logging.Logger logger = Instances.getProject().getLogger();

    public static void info(String info, Object... args) {
        logger.lifecycle(info, args);
    }

    public static void error(String error, Object... args) {
        logger.error(error, args);
    }

    public static void warn(String warn, Object... args) {
        logger.warn(warn, args);
    }
}
