package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableLauncherConfig;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public abstract class DefaultDefaultableLauncherConfig extends GroovyObjectSupport
        implements DefaultableLauncherConfig, ConfigFinalizer {
    @Inject
    public DefaultDefaultableLauncherConfig(Managers managers) {
        getGradleDaemonMaxIdleTime().convention((int) TimeUnit.MINUTES.toSeconds(2));
        getForwardOutput().convention(true);
    }

    @Override
    public void finalizeProps() {
        ClassUtil.finalizeProperties(this, DefaultableLauncherConfig.class);
    }
}
