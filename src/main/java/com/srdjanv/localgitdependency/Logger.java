package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.project.ManagerBase;
import com.srdjanv.localgitdependency.project.ProjectBuilder;

// TODO: 05/03/2023 implement this in a better way
public class Logger extends ManagerBase {
    private final org.gradle.api.logging.Logger logger = getProject().getLogger();

    public Logger(ProjectBuilder projectBuilder) {
        super(projectBuilder);
    }

    public void info(String info, Object... args) {
        logger.lifecycle(info, args);
    }

    public void error(String error, Object... args) {
        logger.error(error, args);
    }

    public void warn(String warn, Object... args) {
        logger.warn(warn, args);
    }
}
