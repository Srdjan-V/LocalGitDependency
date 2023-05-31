package io.github.srdjanv.localgitdependency.config;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.GradleException;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

// TODO: 10/05/2023 Refactor
final class ConfigManager extends ManagerBase implements IConfigManager {
    private PluginConfig pluginConfig;
    private DefaultableConfig defaultableConfig;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        {
            PluginConfig.Builder builder = new PluginConfig.Builder();
            File defaultDir = Constants.defaultDir.apply(getProject());
            builder.persistentDir(Constants.defaultPersistentDir.apply(defaultDir));
            builder.gitDir(Constants.defaultLibsDir.apply(defaultDir));
            builder.mavenDir(Constants.defaultMavenFolder.apply(defaultDir));
            builder.automaticCleanup(true);
            builder.keepInitScriptUpdated(true);
            builder.generateGradleTasks(true);
            builder.generateGradleTasks(true);

            pluginConfig = new PluginConfig(builder, false);
        }

        DefaultableConfig.Builder builder = new DefaultableConfig.Builder();
        builder.dependencyType(Dependency.Type.JarFlatDir);
        builder.keepGitUpdated(true);
        builder.keepInitScriptUpdated(true);
        builder.generateGradleTasks(true);
        builder.generateGradleTasks(true);
        builder.tryGeneratingSourceJar(false);
        builder.tryGeneratingJavaDocJar(false);
        builder.enableIdeSupport(false);
        builder.registerDependencyRepositoryToProject(true);
        builder.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));

        defaultableConfig = new DefaultableConfig(builder, false);
    }


    @Override
    public void configurePlugin(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (pluginConfig.isCustom()) {
            throw new GradleException("You can't change the pluginConfig once its set");
        }
        var pluginConfigBuilder = new  PluginConfig.Builder();
        if (ClosureUtil.delegateNullSafe(configureClosure, pluginConfigBuilder)) {
            pluginConfig = new PluginConfig(pluginConfigBuilder, true);
        }
    }

    @Override
    public void configureDefaultable(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (defaultableConfig.isCustom()) {
            throw new GradleException("You can't change the defaultableConfig once its set");
        }
        var defaultableConfigBuilder = new DefaultableConfig.Builder();
        if (ClosureUtil.delegateNullSafe(configureClosure, defaultableConfigBuilder)) {
            defaultableConfig = new DefaultableConfig(defaultableConfigBuilder, true);
        }

/*        if (configureClosure != null) {
            PluginConfig.Builder builder = new PluginConfig.Builder();
            ClosureUtil.delegate(configureClosure, builder);
            configureFilePaths(builder);
            PluginConfig newGlobalProperty = new PluginConfig(builder);
            customPathsCheck(newGlobalProperty);
            this.pluginConfig = mergeGlobalProperty(pluginConfig, newGlobalProperty);
        }*/
    }

    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public DefaultableConfig getDefaultableConfig() {
        return defaultableConfig;
    }

    private void customPathsCheck(PluginConfig globalProperty) {
        if (globalProperty.getAutomaticCleanup() != null) {
            return;
        }

        Optional<File> optional = streamEssentialDirectories(globalProperty).filter(Objects::nonNull).findAny();
        if (optional.isPresent()) {
            throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
        }
    }

    @Override
    public void createEssentialDirectories() {
        streamEssentialDirectories(pluginConfig).forEach(Constants::checkExistsAndMkdirs);
    }

    private static Stream<File> streamEssentialDirectories(PluginConfig property) {
        return Stream.of(property.getPersistentDir(), property.getGitDir(), property.getMavenDir());
    }


}
