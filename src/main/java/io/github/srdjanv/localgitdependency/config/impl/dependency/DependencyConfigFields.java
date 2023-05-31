package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfigFields;
import io.github.srdjanv.localgitdependency.git.GitInfo;

import java.io.File;

public abstract class DependencyConfigFields extends DefaultableConfigFields {
    protected DependencyConfigFields() {
    }

    protected String url;
    protected String name;
    protected String target;
    protected GitInfo.TargetType targetType;
    protected String configuration;
    protected Closure[] configurations;
    protected Closure[] mappings;
    protected Closure launcher;
    protected File gitDir;
    protected File persistentDir;
    protected File mavenDir;

}
