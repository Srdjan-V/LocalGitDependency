package io.github.srdjanv.localgitdependency.config.impl.dependency;

import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfigFields;
import io.github.srdjanv.localgitdependency.git.GitInfo;

import java.io.File;
import java.util.List;

public abstract class DependencyConfigFields extends DefaultableConfigFields {
    protected DependencyConfigFields() {
    }

    protected String url;
    protected String name;
    protected String target;
    protected GitInfo.TargetType targetType;
    protected String configuration;
    protected List<ConfigurationConfig> configurationConfig;
    protected List<SourceSetMapperConfig> sourceSetMapperConfig;
    protected File gitDir;
    protected File persistentDir;
    protected File mavenDir;

}
