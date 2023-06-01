package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public final class GradleInfo {
    private final Dependency dependency;
    private final GradleLaunchers launchers;
    private final File initScript;
    private final boolean keepInitScriptUpdated;
    private final boolean tryGeneratingSourceJar;
    private final boolean tryGeneratingJavaDocJar;
    private final int gradleDaemonMaxIdleTime;

    public GradleInfo(PluginConfig pluginConfig, DependencyConfig dependencyConfig, Dependency dependency, ErrorUtil errorBuilder) {
        this.dependency = dependency;
        this.launchers = GradleLaunchers.build(dependencyConfig, errorBuilder);
        if (dependencyConfig.getKeepInitScriptUpdated() == null) {
            errorBuilder.append("DependencyConfig: 'keepInitScriptUpdated' is null");
            this.keepInitScriptUpdated = false;
        } else this.keepInitScriptUpdated = dependencyConfig.getKeepInitScriptUpdated();

        if (dependency.getName() != null) {
            File dir;
            if (dependencyConfig.getPersistentDir() != null) {
                dir = dependencyConfig.getPersistentDir();
            } else {
                dir = pluginConfig.getPersistentDir();
            }
            this.initScript = Constants.persistentInitScript.apply(dir,
                    dependency.getName());
        } else this.initScript = null;

        if (dependencyConfig.getTryGeneratingSourceJar() == null) {
            errorBuilder.append("DependencyConfig: 'tryGeneratingSourceJar' is null");
            this.tryGeneratingSourceJar = false;
        } else this.tryGeneratingSourceJar = dependencyConfig.getTryGeneratingSourceJar();

        if (dependencyConfig.getTryGeneratingJavaDocJar() == null) {
            errorBuilder.append("DependencyConfig: 'tryGeneratingJavaDocJar' is null");
            this.tryGeneratingJavaDocJar = false;
        } else this.tryGeneratingJavaDocJar = dependencyConfig.getTryGeneratingJavaDocJar();

        if (dependencyConfig.getGradleDaemonMaxIdleTime() == null) {
            errorBuilder.append("DependencyConfig: 'gradleDaemonMaxIdleTime' is null");
            this.gradleDaemonMaxIdleTime = 0;
        } else this.gradleDaemonMaxIdleTime = dependencyConfig.getGradleDaemonMaxIdleTime();
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @NotNull
    public File getInitScript() {
        return initScript;
    }

    public boolean isKeepInitScriptUpdated() {
        return keepInitScriptUpdated;
    }

    public boolean isTryGeneratingSourceJar() {
        return tryGeneratingSourceJar;
    }

    public boolean isTryGeneratingJavaDocJar() {
        return tryGeneratingJavaDocJar;
    }

    public int getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    @NotNull
    public GradleLaunchers getLaunchers() {
        return launchers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradleInfo gitInfo = (GradleInfo) o;
        return Objects.equals(gitInfo.getDependency().getName(), getDependency().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency.getName());
    }
}
