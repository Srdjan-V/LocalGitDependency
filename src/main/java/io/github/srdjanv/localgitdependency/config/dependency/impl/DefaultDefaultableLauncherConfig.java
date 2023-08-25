package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableLauncherConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public abstract class DefaultDefaultableLauncherConfig extends GroovyObjectSupport implements DefaultableLauncherConfig, ConfigFinalizer {
    @Inject
    public DefaultDefaultableLauncherConfig(Managers managers) {
        gradleDaemonMaxIdleTime().convention((int) TimeUnit.MINUTES.toSeconds(2));
        forwardOutput().convention(true);
    }

    @Override
    public void finalizeProps() {

    }
}
