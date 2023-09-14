package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.gradle.api.provider.Property;

public abstract class DefaultDependencyConfig extends GroovyObjectSupport implements DependencyConfig, ConfigFinalizer {
    private final LauncherConfig launcherConfig;
    private final Property<Dependency> dependencyCallBack;

    @Inject
    public DefaultDependencyConfig(final String url, final Managers managers) {
        getUrl().convention(url).finalizeValue();
        getName().convention(managers.getProject().provider(() -> getNameFromUrl(getUrl().get())));
        dependencyCallBack = managers.getProject().getObjects().property(Dependency.class);

        final var defaultable = managers.getConfigManager().getDefaultableConfig();
        getDefaultTargetType().convention(managers.getProject().provider(() -> GitInfo.TargetType.BRANCH));
        getKeepGitUpdated().convention(managers.getProject().provider(() -> {
            var mapper = dependencyCallBack.get().getSourceSetMapper();
            if (mapper != null && mapper.getMappings().isEmpty()) {
                return false;
            }
            return defaultable.getKeepGitUpdated().get();
        }));
        getKeepInitScriptUpdated()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getKeepInitScriptUpdated().get()));
        getGenerateGradleTasks()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getGenerateGradleTasks().get()));
        getTryGeneratingSourceJar()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getTryGeneratingSourceJar().get()));
        getTryGeneratingJavaDocJar()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getTryGeneratingJavaDocJar().get()));
        getDependencyTags().convention(managers.getProject().provider(() -> {
            var defaultDepTags = defaultable.getDependencyTags().get();
            var depTags = managers.getDependencyManager().getDepTags(getName().get());
            if (depTags == null) return defaultDepTags;
            var newSet = new HashSet<>(defaultDepTags);
            newSet.addAll(depTags);
            return newSet;
        }));
        launcherConfig = managers.getProject().getObjects().newInstance(DefaultLauncherConfig.class, managers, this);
    }

    public Property<Dependency> getDependencyCallBack() {
        return dependencyCallBack;
    }

    @Override
    public LauncherConfig getBuildLauncher() {
        return launcherConfig;
    }

    @Override
    public void finalizeProps() {
        ClassUtil.finalizeProperties(this, DependencyConfig.class);
        ((DefaultLauncherConfig) getBuildLauncher()).finalizeProps();
    }

    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1).replace(".", "") : null;
    }
}
