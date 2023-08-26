package io.github.srdjanv.localgitdependency.config;

public class PluginConfigTests {
  /*  private static final List<PluginConfigMapper<?>> mappers = new ArrayList<>();
    private static final PluginConfigMapper<Boolean> keepInitScriptUpdated;
    private static final PluginConfigMapper<Boolean> generateGradleTasks;
    private static final PluginConfigMapper<Boolean> automaticCleanup;
    private static final PluginConfigMapper<File> defaultDir;
    private static final PluginConfigMapper<File> gitDir;
    private static final PluginConfigMapper<File> persistentDir;
    private static final PluginConfigMapper<File> mavenDir;

    static {
        keepInitScriptUpdated = PluginConfigMapper.create(mapper -> {
            mapper.setName("keepInitScriptUpdated");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getKeepInitScriptUpdated());
            mapper.setValueGetter(PluginConfig::getKeepInitScriptUpdated);
            mapper.setBuilder(PluginConfig.Builder::keepInitScriptUpdated);
        });
        generateGradleTasks = PluginConfigMapper.create(mapper -> {
            mapper.setName("generateGradleTasks");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getGenerateGradleTasks());
            mapper.setValueGetter(PluginConfig::getGenerateGradleTasks);
            mapper.setBuilder(PluginConfig.Builder::generateGradleTasks);
        });
        automaticCleanup = PluginConfigMapper.create(mapper -> {
            mapper.setName("automaticCleanup");
            mapper.setNewValue(pluginConfig -> !pluginConfig.getAutomaticCleanup());
            mapper.setValueGetter(PluginConfig::getAutomaticCleanup);
            mapper.setBuilder(PluginConfig.Builder::automaticCleanup);
        });
        defaultDir = PluginConfigMapper.create(mapper -> {
            mapper.setName("defaultDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir().getParent(), "newDefaultDir"));
            mapper.setValueGetter(PluginConfig::getDefaultDir);
            mapper.setBuilder(PluginConfig.Builder::defaultDir);
        });
        gitDir = PluginConfigMapper.create(mapper -> {
            mapper.setName("gitDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newLib"));
            mapper.setValueGetter(PluginConfig::getGitDir);
            mapper.setBuilder(PluginConfig.Builder::gitDir);
        });
        persistentDir = PluginConfigMapper.create(mapper -> {
            mapper.setName("persistentDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newPersistentDir"));
            mapper.setValueGetter(PluginConfig::getPersistentDir);
            mapper.setBuilder(PluginConfig.Builder::persistentDir);
        });
        mavenDir = PluginConfigMapper.create(mapper -> {
            mapper.setName("mavenDir");
            mapper.setNewValue(pluginConfig -> new File(pluginConfig.getDefaultDir(), "newMavenDir"));
            mapper.setValueGetter(PluginConfig::getMavenDir);
            mapper.setBuilder(PluginConfig.Builder::mavenDir);
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

                        configManager.finalizeConfigs();
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

                        Assertions.assertThrows(GradleException.class, configManager::finalizeConfigs);
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> testDefaultDir() {
        return Stream.of(gitDir, persistentDir, mavenDir).
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new PluginTestExecutable<File>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        var defaultDirFile = defaultDir.getNewValueType().apply(defaultPluginConfigs);
                        var newPathFileName = (mapper.getNewValueType().apply(defaultPluginConfigs)).getName();

                        configManager.configurePlugin(ClosureUtil.<PluginConfig.Builder>configure(builder -> {
                            builder.defaultDir(defaultDirFile);
                            mapper.getBuilderConfig().accept(builder, new File(newPathFileName));
                            builder.automaticCleanup(false);
                        }));

                        configManager.finalizeConfigs();
                        Assertions.assertEquals(new File(defaultDirFile, newPathFileName).toString(),
                                mapper.getValueGetter().apply(configManager.getPluginConfig()).toString());
                    }
                }));
    }


    private static class PluginConfigMapper<T> extends ConfigMapper<PluginConfig, PluginConfig.Builder, T> {

        private static <T> PluginConfigMapper<T> create(Consumer<PluginConfigMapper<T>> config) {
            var mapper = new PluginConfigMapper<T>();
            config.accept(mapper);
            PluginConfigTests.mappers.add(mapper);
            return mapper;
        }

        private PluginConfigMapper() {
        }

    }

    private static abstract class PluginTestExecutable<T> implements Executable {
        PluginConfigMapper<T> mapper;
        LGDManagers lgdInstance;
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
*/
}
