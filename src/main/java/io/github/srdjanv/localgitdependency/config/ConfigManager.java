package io.github.srdjanv.localgitdependency.config;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableLauncherConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfigFields;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.gradle.api.GradleException;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.srdjanv.localgitdependency.util.FileUtil.checkExistsAndMkdirs;

final class ConfigManager extends ManagerBase implements IConfigManager {
    private PluginConfig pluginConfig;
    private PluginConfig.Builder pluginConfigBuilder;
    private boolean pluginConfigBuilderConfigured;
    private DefaultableConfig defaultableConfig;
    private DefaultableConfig.Builder defaultableConfigBuilder;
    private boolean defaultableConfigBuilderConfigured;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        pluginConfigBuilder = new PluginConfig.Builder();
        defaultableConfigBuilder = new DefaultableConfig.Builder();
    }

    @Override
    public void configurePlugin(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (ClosureUtil.delegateNullSafe(configureClosure, pluginConfigBuilder)) {
            pluginConfigBuilderConfigured = true;
        }
    }

    @Override
    public void configureDefaultable(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (ClosureUtil.delegateNullSafe(configureClosure, defaultableConfigBuilder)) {
            defaultableConfigBuilderConfigured = true;
        }
    }

    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public DefaultableConfig getDefaultableConfig() {
        return defaultableConfig;
    }

    @Override
    public void configureConfigs() {
        var defaultPluginConfig = defaultPluginConfig();
        if (!pluginConfigBuilderConfigured) {
            pluginConfig = defaultPluginConfig;
        } else {
            pluginConfig = new PluginConfig(pluginConfigBuilder, getDefaultDir());
            pluginConfigBuilder = null;
            customPathsCheck(pluginConfig);
            ClassUtil.mergeObjectsDefaultReference(pluginConfig, defaultPluginConfig, PluginConfigFields.class);
            var nulls = ClassUtil.validData(PluginConfigFields.class, pluginConfig);
            if (!nulls.isEmpty())
                ErrorUtil.create("Unable to configurePlugin some fields are null:").append(nulls).throwGradleException();

        }

        var defaultDefaultableConfig = defaultDefaultableConfig();
        if (!defaultableConfigBuilderConfigured) {
            defaultableConfig = defaultDefaultableConfig;
        } else {
            defaultableConfig = new DefaultableConfig(defaultableConfigBuilder, defaultDefaultableConfig);
            defaultableConfigBuilder = null;
            var nulls = ClassUtil.validData(defaultableConfig);
            if (!nulls.isEmpty())
                ErrorUtil.create("Unable to configureDefaultable some fields are null:").append(nulls).throwGradleException();
        }
    }

    private void customPathsCheck(PluginConfig pluginConfig) {
        var customPaths = streamAllDirectories(pluginConfig).filter(Objects::nonNull).collect(Collectors.toList());

        if (pluginConfig.getAutomaticCleanup() == null) {
            if (customPaths.size() == 0) return;
            throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
        }
        if (pluginConfig.getAutomaticCleanup()) {
            if (customPaths.size() == 0) return;

            if (customPaths.contains(pluginConfig.getDefaultDir())) {
                if (!getProject().getLayout().getProjectDirectory().getAsFile().equals(pluginConfig.getDefaultDir().getParentFile())) {
                    PluginLogger.warn("The default directory in not in the root project and automatic cleanup is on, this might delete unwanted directory's if configured incorrectly");
                }
            }
            PluginLogger.warn("Custom global directory paths detected and automatic cleanup is on, this might delete unwanted directory's if configured incorrectly");
        }
    }

    @Override
    public void createEssentialDirectories() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        streamEssentialDirectories(pluginConfig).forEach(file -> {
            if (file.exists()) {
                atomicBoolean.set(true);
            }
        });

        boolean createPersistentDir = false;
        boolean createGitDir = false;
        boolean createMavenDir = false;

        if (atomicBoolean.get()) {
            var deps = getDependencyManager().getDependencies();
            if (!deps.isEmpty()) {
                createPersistentDir = true;
                createGitDir = true;
                for (Dependency dependency : deps)
                    switch (dependency.getDependencyType()) {
                        case MavenProjectLocal, MavenProjectDependencyLocal -> createMavenDir = true;
                    }
            }
        }

        if (createPersistentDir) checkExistsAndMkdirs(pluginConfig.getPersistentDir());
        if (createGitDir) checkExistsAndMkdirs(pluginConfig.getGitDir());
        if (createMavenDir) checkExistsAndMkdirs(pluginConfig.getMavenDir());
    }

    private static Stream<File> streamEssentialDirectories(PluginConfig pluginConfig) {
        return Stream.of(pluginConfig.getPersistentDir(), pluginConfig.getGitDir(), pluginConfig.getMavenDir());
    }

    private static Stream<File> streamAllDirectories(PluginConfig pluginConfig) {
        return Stream.of(pluginConfig.getDefaultDir(), pluginConfig.getPersistentDir(), pluginConfig.getGitDir(), pluginConfig.getMavenDir());
    }

    PluginConfig defaultPluginConfig() {
        File defaultDir = getDefaultDir();

        PluginConfig.Builder builder = new PluginConfig.Builder();
        builder.defaultDir(defaultDir);
        builder.persistentDir(Constants.defaultPersistentDir.apply(defaultDir));
        builder.gitDir(Constants.defaultLibsDir.apply(defaultDir));
        builder.mavenDir(Constants.defaultMavenFolder.apply(defaultDir));
        builder.automaticCleanup(true);
        builder.keepInitScriptUpdated(true);
        builder.generateGradleTasks(true);
        builder.generateGradleTasks(true);

        return new PluginConfig(builder, defaultDir);
    }

    DefaultableConfig defaultDefaultableConfig() {
        DefaultableConfig.Builder builder = new DefaultableConfig.Builder();
        builder.dependencyType(Dependency.Type.JarFlatDir);
        builder.keepGitUpdated(true);
        builder.keepInitScriptUpdated(true);
        builder.generateGradleTasks(true);
        builder.tryGeneratingSourceJar(false);
        builder.tryGeneratingJavaDocJar(false);
        builder.enableIdeSupport(false);
        builder.registerDependencyRepositoryToProject(true);
        builder.buildLauncher(ClosureUtil.<DefaultableLauncherConfig.Builder>configure(launcher -> {
            launcher.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));
            launcher.forwardOutput(true);
        }));

        return new DefaultableConfig(builder);
    }

    File getDefaultDir() {
        return Constants.defaultDir.apply(getProject());
    }
}
