package io.github.srdjanv.localgitdependency.config;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfigFields;
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
    private DefaultableConfig defaultableConfig;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        {
            PluginConfig.Builder builder = new PluginConfig.Builder();
            File defaultDir = Constants.defaultDir.apply(getProject());
            builder.defaultDir(defaultDir);
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
        builder.buildLauncher(ClosureUtil.ofDelegate(launcherObj -> {
            // TODO: 01/06/2023
            var launcher = (LauncherBuilder) launcherObj;
            launcher.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));
            launcher.startup(ClosureUtil.ofDelegate(obj -> obj));
            launcher.probe(ClosureUtil.ofDelegate(obj -> obj));
            launcher.build(ClosureUtil.ofDelegate(obj -> obj));
            return launcherObj;
        }));

        defaultableConfig = new DefaultableConfig(builder, false);
    }


    @Override
    public void configurePlugin(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (pluginConfig.isCustom()) {
            throw new GradleException("You can't change the pluginConfig once its set");
        }
        var pluginConfigBuilder = new PluginConfig.Builder();
        if (ClosureUtil.delegateNullSafe(configureClosure, pluginConfigBuilder)) {
            var newPluginConfig = new PluginConfig(pluginConfigBuilder, true);
            customPathsCheck(newPluginConfig);
            ClassUtil.mergeObjects(newPluginConfig, pluginConfig, PluginConfigFields.class);
            var list = ClassUtil.validateData(newPluginConfig, PluginConfigFields.class);
            if (list != null) {
                list.add(0, "Unable to configurePlugin some fields are null:");
                throw new GradleException(list.stream().collect(Collectors.joining(Constants.TAB_INDENT, System.lineSeparator(), "")));
            } else {
                pluginConfig = newPluginConfig;
            }
        }
    }

    @Override
    public void configureDefaultable(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (defaultableConfig.isCustom()) {
            throw new GradleException("You can't change the defaultableConfig once its set");
        }
        var defaultableConfigBuilder = new DefaultableConfig.Builder();
        if (ClosureUtil.delegateNullSafe(configureClosure, defaultableConfigBuilder)) {
            var newDefaultableConfig = new DefaultableConfig(defaultableConfigBuilder, true);
            ClassUtil.mergeObjects(newDefaultableConfig, defaultableConfig, DefaultableConfigFields.class);
            var list = ClassUtil.validateData(newDefaultableConfig, DefaultableConfigFields.class);
            if (list != null) {
                list.add(0, "Unable to configureDefaultable some fields are null:");
                throw new GradleException(list.stream().collect(Collectors.joining(Constants.TAB_INDENT, System.lineSeparator(), "")));
            } else {
                defaultableConfig = newDefaultableConfig;
            }
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

    private void customPathsCheck(PluginConfig pluginConfig) {
        if (pluginConfig.getAutomaticCleanup() == null) {
            Optional<File> optional = streamEssentialDirectories(pluginConfig).filter(Objects::nonNull).findAny();
            if (optional.isPresent()) {
                throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
            }
            return;
        }
        if (pluginConfig.getAutomaticCleanup() == true) {
            Optional<File> optional = streamEssentialDirectories(pluginConfig).filter(Objects::nonNull).findAny();
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
            }
            for (Dependency dependency : deps) {
                switch (dependency.getDependencyType()) {
                    case MavenProjectLocal:
                    case MavenProjectDependencyLocal:
                        createMavenDir = true;
                }
            }
        }

        if (createPersistentDir) {
            checkExistsAndMkdirs(pluginConfig.getPersistentDir());
        }
        if (createGitDir) {
            checkExistsAndMkdirs(pluginConfig.getGitDir());
        }
        if (createMavenDir) {
            checkExistsAndMkdirs(pluginConfig.getMavenDir());
        }
    }

    private static Stream<File> streamEssentialDirectories(PluginConfig pluginConfig) {
        return Stream.of(pluginConfig.getPersistentDir(), pluginConfig.getGitDir(), pluginConfig.getMavenDir());
    }
}
