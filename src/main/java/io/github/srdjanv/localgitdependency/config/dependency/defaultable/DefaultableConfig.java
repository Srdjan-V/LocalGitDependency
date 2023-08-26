package io.github.srdjanv.localgitdependency.config.dependency.defaultable;

import io.github.srdjanv.localgitdependency.config.dependency.common.CommonConfig;
import org.gradle.api.provider.Property;

public interface DefaultableConfig extends CommonConfig {
    /**
     * @see DefaultableLauncherConfig
     */
    Property<DefaultableLauncherConfig> getBuildLauncher();
}
