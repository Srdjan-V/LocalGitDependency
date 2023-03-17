package io.github.srdjanv.localgitdependency.logger;

import io.github.srdjanv.localgitdependency.Constants;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class PluginLogger {
    private PluginLogger() {
    }

    final static Logger logger = Logging.getLogger("local-git-dependency");

    public static void info(String info, Project projectName, Object... args) {
        logger.lifecycle(info, projectName.getName(), args);
    }

    public static void info(String info, Object... args) {
        logger.lifecycle(Constants.TAB_INDENT + info, args);
    }

}
