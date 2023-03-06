package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.project.ManagerBase;
import com.srdjanv.localgitdependency.project.ProjectInstances;

// TODO: 05/03/2023 implement this in a better way
public class Logger extends ManagerBase {
    private org.gradle.api.logging.Logger logger;
    public Logger(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {
        logger = getProject().getLogger();
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
