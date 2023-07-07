package io.github.srdjanv.localgitdependency.config;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfigFields;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.GradleException;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.srdjanv.localgitdependency.util.FileUtil.checkExistsAndMkdirs;

// TODO: 10/05/2023 Refactor
final class ConfigManager extends ManagerBase implements IConfigManager {
    private PluginConfig pluginConfig;
    private PluginConfig.Builder pluginConfigBuilder;
    private DefaultableConfig defaultableConfig;
    private DefaultableConfig.Builder defaultableConfigBuilder;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        pluginConfigBuilder = new PluginConfig.Builder();
        defaultableConfigBuilder = new DefaultableConfig.Builder();
    }

    @Override
    public void configurePlugin(Closure configureClosure) {
        ClosureUtil.delegateNullSafe(configureClosure, pluginConfigBuilder);
    }

    @Override
    public void configureDefaultable(@SuppressWarnings("rawtypes") Closure configureClosure) {
        ClosureUtil.delegateNullSafe(configureClosure, defaultableConfigBuilder);
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
        if (pluginConfigBuilder == null) {
            pluginConfig = defaultPluginConfig;
        } else {
            pluginConfig = new PluginConfig(pluginConfigBuilder, getDefaultDir());
            pluginConfigBuilder = null;
            customPathsCheck(pluginConfig);
            ClassUtil.mergeObjectsDefaultReference(pluginConfig, defaultPluginConfig, PluginConfigFields.class);
            var list = ClassUtil.validateDataDefault(pluginConfig, PluginConfigFields.class);
            if (list.size() != 0) {
                list.add(0, "Unable to configurePlugin some fields are null:");
                throw new GradleException(list.stream().collect(Collectors.joining(Constants.TAB_INDENT, System.lineSeparator(), "")));
            }
        }

        var defaultDefaultableConfig = defaultDefaultableConfig();
        if (defaultableConfigBuilder == null) {
            defaultableConfig = defaultDefaultableConfig;
        } else {
            defaultableConfig = new DefaultableConfig(defaultableConfigBuilder, defaultDefaultableConfig);
            defaultableConfigBuilder = null;
            var list = ClassUtil.validateDataDefault(defaultableConfig);
            if (list.size() != 0) {
                list.add(0, "Unable to configureDefaultable some fields are null:");
                throw new GradleException(list.stream().collect(Collectors.joining(Constants.TAB_INDENT, System.lineSeparator(), "")));
            }
        }
    }

    private void customPathsCheck(PluginConfig pluginConfig) {
        if (pluginConfig.getAutomaticCleanup() == null) {
            Optional<File> optional = streamAllDirectories(pluginConfig).filter(Objects::nonNull).findAny();
            if (optional.isPresent()) {
                if (optional.get().equals(getDefaultDir())) return;
                throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
            }
            return;
        }
        if (pluginConfig.getAutomaticCleanup()) {
            Optional<File> optional = streamAllDirectories(pluginConfig).filter(Objects::nonNull).findAny();
            if (optional.isPresent()) {
                PluginLogger.warn("Custom global directory paths detected and automatic cleanup is on, this might delete unwanted directory's if configured incorrectly");
            }
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
        builder.buildLauncher(ClosureUtil.<LauncherBuilder>configure(launcher -> {
            launcher.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));
            launcher.forwardOutput(true);
        }));

        return new DefaultableConfig(builder);
    }

    File getDefaultDir() {
        return Constants.defaultDir.apply(getProject());
    }
}
