package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.impl.dependency.ConfigurationConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class Configuration {
    public static List<Configuration> build(DependencyConfig dependencyConfig) {
        var configurations = new ArrayList<Configuration>();
        if (dependencyConfig.getConfigurations() == null) {
            if (dependencyConfig.getConfiguration() != null) {
                var configurationBuilder = new ConfigurationConfig.Builder();
                configurationBuilder.configuration(dependencyConfig.getConfiguration());
                configurations.add(new Configuration(new ConfigurationConfig(configurationBuilder)));
            }
        } else {
            for (var configurationClosure : dependencyConfig.getConfigurations()) {
                var builder = new ConfigurationConfig.Builder();
                ClosureUtil.delegate(configurationClosure, builder);
                configurations.add(new Configuration(new ConfigurationConfig(builder)));
            }
        }
        return Collections.unmodifiableList(configurations);
    }

    private final String configuration;
    private final Closure closure;
    private final List<Property> artifactProperty;

    public Configuration(ConfigurationConfig configurationConfig) {
        this.configuration = configurationConfig.getConfiguration();
        this.closure = configurationConfig.getClosure();

        Set<Property> properties = new HashSet<>();
        if (configurationConfig.getIncludeNotations() != null)
            for (String includeNotation : configurationConfig.getIncludeNotations()) {
                Property prop;
                var closure = configurationConfig.getClosureMap().get(includeNotation);
                if (closure == null) {
                    prop = new Property(includeNotation, this.closure);
                } else {
                    prop = new Property(includeNotation, closure);
                }
                properties.add(prop);
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
