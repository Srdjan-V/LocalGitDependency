package io.github.srdjanv.localgitdependency.config.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableLauncherConfig;
import org.gradle.api.provider.Property;

public interface LauncherConfig extends DefaultableLauncherConfig {
    Property<Launchers.Startup> startup();
    Property<Launchers.Probe> probe();
    Property<Launchers.Build> build();
}
