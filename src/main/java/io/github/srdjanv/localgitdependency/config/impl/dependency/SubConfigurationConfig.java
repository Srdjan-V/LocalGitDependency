package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.SubConfigurationBuilder;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SubConfigurationConfig {
    private final String name;
    private final String configuration;
    private List<ConfigurationConfig> configurationConfigs;
    public SubConfigurationConfig(Builder builder) {
        name = builder.name;
        configuration = builder.configuration;

        if (builder.configurations != null) {
            List<ConfigurationConfig> configurationConfigList = new ArrayList<>();
            for (Closure closure : builder.configurations) {
                var configurationConfig = new ConfigurationConfig.Builder();
                if (ClosureUtil.delegateNullSafe(closure, configurationConfig)) {
                    configurationConfigList.add(new ConfigurationConfig(configurationConfig));
                } else throw new GradleException("Null provided as a configuration closure");
            }
            this.configurationConfigs = configurationConfigList;
        }
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getConfiguration() {
        return configuration;
    }

    @Nullable
    public List<ConfigurationConfig> getConfigurationConfigs() {
        return configurationConfigs;
    }

    public static class Builder implements SubConfigurationBuilder {
        private String name;
        private String configuration;
        private Closure[] configurations;

        @Override
        public void name(String name) {
            this.name = name;
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void configuration(Closure... configurations) {
            this.configurations = configurations;
        }
    }

}
