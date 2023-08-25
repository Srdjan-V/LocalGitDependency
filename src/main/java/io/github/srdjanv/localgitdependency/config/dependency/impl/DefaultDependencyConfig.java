package io.github.srdjanv.localgitdependency.config.dependency.impl;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultDependencyConfig implements DependencyConfig {
    @Inject
    public DefaultDependencyConfig(String url, Managers managers) {
        url().convention(url);

        // TODO: 25/08/2023 conventions
    }
}
