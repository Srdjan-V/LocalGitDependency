package io.github.srdjanv.localgitdependency.config.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableLauncherConfig;
import org.gradle.api.provider.Property;

public interface LauncherConfig extends DefaultableLauncherConfig {
    Launchers.Startup getStartup();
    Launchers.Probe getProbe();
    Launchers.Build getBuild();
}
