package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultLauncherConfig extends GroovyObjectSupport implements LauncherConfig, ConfigFinalizer {
    @Inject
    public DefaultLauncherConfig(String url, Managers managers) {

    }
}
