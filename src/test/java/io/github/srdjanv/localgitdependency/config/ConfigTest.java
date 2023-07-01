package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfigFields;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyManagerInstance;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ConfigTest {
    private static final List<PluginConfigMapper<?>> mappers = new ArrayList<>();
    private static final PluginConfigMapper<Boolean> keepInitScriptUpdated;
    private static final PluginConfigMapper<Boolean> generateGradleTasks;
    private static final PluginConfigMapper<Boolean> automaticCleanup;
    private static final PluginConfigMapper<File> defaultDir;
    private static final PluginConfigMapper<File> gitDir;
    private static final PluginConfigMapper<File> persistentDir;
    private static final PluginConfigMapper<File> mavenDir;

    static {
        keepInitScriptUpdated = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("keepInitScriptUpdated");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getKeepInitScriptUpdated());
            mapper.setValueGetter(PluginConfig::getKeepInitScriptUpdated);
            mapper.setBuilder((PluginConfig.Builder::keepInitScriptUpdated));
        });
        generateGradleTasks = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("generateGradleTasks");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getGenerateGradleTasks());
            mapper.setValueGetter(PluginConfig::getGenerateGradleTasks);
            mapper.setBuilder((PluginConfig.Builder::generateGradleTasks));
        });
        automaticCleanup = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("automaticCleanup");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getAutomaticCleanup());
            mapper.setValueGetter(PluginConfig::getAutomaticCleanup);
            mapper.setBuilder((PluginConfig.Builder::automaticCleanup));
        });
        defaultDir = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("defaultDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir().getParent(), "newDefaultDir"));
            mapper.setValueGetter(PluginConfig::getDefaultDir);
            mapper.setBuilder((PluginConfig.Builder::defaultDir));
        });
        gitDir = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("gitDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newLib"));
            mapper.setValueGetter(PluginConfig::getGitDir);
            mapper.setBuilder((PluginConfig.Builder::gitDir));
        });
        persistentDir = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("persistentDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newPersistentDir"));
            mapper.setValueGetter(PluginConfig::getPersistentDir);
            mapper.setBuilder((PluginConfig.Builder::persistentDir));
        });
        mavenDir = PluginConfigMapper.create(mappers, mapper -> {
            mapper.setName("mavenDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newMavenDir"));
            mapper.setValueGetter(PluginConfig::getMavenDir);
            mapper.setBuilder((PluginConfig.Builder::mavenDir));
        });
    }

    @Test
    void testMapperCount() {
        Assertions.assertEquals(PluginConfigFields.class.getDeclaredFields().length, mappers.size());
    }

    @TestFactory
    Stream<DynamicTest> testIndividualPluginConfigs() {
        return mappers.stream().
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new PluginTestExecutable<>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        configManager.configurePlugin(ClosureUtil.<PluginConfig.Builder>configure(builder -> {
                            mapper.getBuilderConfig().accept(builder, mapper.getNewValue().apply(defaultPluginConfigs));
                            if (!mapper.getName().equals(automaticCleanup.getName())) {
                                builder.automaticCleanup(false);
                            }
                        }));

                        configManager.configureConfigs();
                        Assertions.assertEquals(mapper.getNewValue().apply(defaultPluginConfigs), mapper.getValueGetter().apply(configManager.getPluginConfig()));
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> testCustomPathsPluginConfigs() {
        return Stream.of(defaultDir, gitDir, persistentDir, mavenDir).
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new PluginTestExecutable<>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        configManager.configurePlugin(ClosureUtil.<PluginConfig.Builder>configure(builder -> {
                            mapper.getBuilderConfig().accept(builder, mapper.getNewValue().apply(defaultPluginConfigs));
                        }));

                        Assertions.assertThrows(GradleException.class, configManager::configureConfigs);
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> testDefaultDir() {
        return Stream.of(gitDir, persistentDir, mavenDir).
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new PluginTestExecutable<>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        var defaultDirFile = (File) defaultDir.getNewValue().apply(defaultPluginConfigs);
                        var newPathFileName = ((File) mapper.getNewValue().apply(defaultPluginConfigs)).getName();

                        configManager.configurePlugin(ClosureUtil.<PluginConfig.Builder>configure(builder -> {
                            builder.defaultDir(defaultDirFile);
                            mapper.getBuilderConfig().accept(builder, new File(newPathFileName));
                            builder.automaticCleanup(false);
                        }));

                        configManager.configureConfigs();
                        Assertions.assertEquals(new File(defaultDirFile, newPathFileName).toString(),
                                mapper.getValueGetter().apply(configManager.getPluginConfig()).toString());
                    }
                }));
    }


    private static abstract class PluginTestExecutable<T> implements Executable {
        PluginConfigMapper<T> mapper;
        LocalGitDependencyManagerInstance lgdInstance;
        IConfigManager configManager;
        PluginConfig defaultPluginConfigs;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public PluginTestExecutable(PluginConfigMapper mapper, Project project) {
            this.mapper = (PluginConfigMapper<T>) mapper;
            lgdInstance = ProjectInstance.getLGDManager(project);
            configManager = lgdInstance.getConfigManager();
            defaultPluginConfigs = ((ConfigManager) configManager).defaultPluginConfig();
        }
    }

}
