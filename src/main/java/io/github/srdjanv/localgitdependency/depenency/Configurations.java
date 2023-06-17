package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.impl.dependency.ConfigurationConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.SubConfigurationConfig;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class Configurations {
    public static List<Configuration> build(DependencyConfig dependencyConfig) {
        return build(dependencyConfig.getConfigurations(), dependencyConfig.getConfiguration());
    }

    private static List<Configuration> build(List<ConfigurationConfig> configurations, String configuration) {
        var builtConfigurations = new ArrayList<Configuration>();
        if (configurations == null) {
            if (configuration != null) {
                var configurationBuilder = new ConfigurationConfig.Builder();
                configurationBuilder.configuration(configuration);
                builtConfigurations.add(new Configuration(new ConfigurationConfig(configurationBuilder)));
            }
        } else {
            for (var configurationConfig : configurations) {
                builtConfigurations.add(new Configuration(configurationConfig, configuration));
            }
        }
        return Collections.unmodifiableList(builtConfigurations);
    }

    public static List<SubConfiguration> buildSub(DependencyConfig dependencyConfig, ErrorUtil errorBuilder) {
        var configurations = new ArrayList<SubConfiguration>();
        if (dependencyConfig.getSubConfigurations() != null) {
            for (var configurationConfig : dependencyConfig.getSubConfigurations()) {
                configurations.add(new SubConfiguration(configurationConfig, errorBuilder));
            }
        }
        return Collections.unmodifiableList(configurations);
    }

    public static class Configuration {
        private final String configuration;
        private final Closure closure;
        private final List<Property> artifactProperty;

        public Configuration(ConfigurationConfig configurationConfig, String defaultConfiguration) {
            if (configurationConfig.getConfiguration() == null) {
                this.configuration = defaultConfiguration;
            } else this.configuration = configurationConfig.getConfiguration();
            this.closure = configurationConfig.getClosure();

            Set<Property> properties = new HashSet<>();
            if (configurationConfig.getIncludeNotations() != null)
                for (String includeNotation : configurationConfig.getIncludeNotations()) {
                    properties.add(new Property(includeNotation, closure));
                }

            if (configurationConfig.getClosureMap() != null)
                for (Map.Entry<String, Closure> entry : configurationConfig.getClosureMap().entrySet()) {
                    if (entry.getKey() == null || entry.getValue() == null) continue;
                    properties.add(new Property(entry.getKey(), entry.getValue()));
                }

            if (configurationConfig.getExcludeNotations() != null)
                for (String includeNotation : configurationConfig.getExcludeNotations()) {
                    Property prop = new Property(includeNotation);
                    if (!properties.add(prop)) {
                        properties.remove(prop);
                        properties.add(prop);
                    }
                }
            this.artifactProperty = Collections.unmodifiableList(new ArrayList<>(properties));
        }

        public Configuration(ConfigurationConfig configurationConfig) {
           this(configurationConfig, null);
        }

        public String getConfiguration() {
            return configuration;
        }

        public Closure closure() {
            return closure;
        }

        @NotNull
        @Unmodifiable
        public List<Property> getArtifactProperty() {
            return artifactProperty;
        }
    }

    public static class SubConfiguration {
        private final String name;
        private final List<Configuration> configurations;

        public SubConfiguration(SubConfigurationConfig config, ErrorUtil errorBuilder) {
            if (config.getName() == null) {
                errorBuilder.append("SubConfiguration: name cant be null");
                name = null;
            } else this.name = config.getName();
            configurations = build(config.getConfigurationConfigs(), config.getConfiguration());
        }


        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public List<Configuration> getConfigurations() {
            return configurations;
        }
    }

    public static class Property {
        private final String notation;
        private final boolean include;
        private final Closure closure;

        private Property(String notation, Closure closure) {
            this.notation = notation;
            this.closure = closure;
            this.include = true;
        }

        private Property(String notation) {
            this.notation = notation;
            this.closure = null;
            this.include = false;
        }

        public String notation() {
            return notation;
        }

        public boolean include() {
            return include;
        }

        public Closure closure() {
            return closure;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Property property = (Property) o;
            return notation.equals(property.notation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(notation);
        }
    }

}
