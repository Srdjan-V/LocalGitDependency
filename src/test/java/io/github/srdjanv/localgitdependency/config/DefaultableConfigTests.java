package io.github.srdjanv.localgitdependency.config;

public class DefaultableConfigTests {
 /*   private static final List<DefaultableConfigMapper<?>> mappers = new ArrayList<>();

    private static final DefaultableConfigMapper<Boolean> keepGitUpdated;
    private static final DefaultableConfigMapper<Boolean> keepInitScriptUpdated;
    private static final DefaultableConfigMapper<Dependency.Type> dependencyType;
    private static final DefaultableConfigMapper<Boolean> tryGeneratingSourceJar;
    private static final DefaultableConfigMapper<Boolean> tryGeneratingJavaDocJar;
    private static final DefaultableConfigMapper<Boolean> enableIdeSupport;
    private static final DefaultableConfigMapper<Boolean> registerDependencyRepositoryToProject;
    private static final DefaultableConfigMapper<Boolean> generateGradleTasks;
  //  private static final DefaultableConfigMapper<DefaultableLauncherConfig> launcher;

    static {
        keepGitUpdated = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("keepGitUpdated");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getKeepGitUpdated());
            mapper.setValueGetter(DefaultableConfig::getKeepGitUpdated);
            mapper.setBuilder(DefaultableConfig.Builder::keepGitUpdated);
        });
        keepInitScriptUpdated = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("keepInitScriptUpdated");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getKeepDependencyInitScriptUpdated());
            mapper.setValueGetter(DefaultableConfig::getKeepDependencyInitScriptUpdated);
            mapper.setBuilder(DefaultableConfig.Builder::keepInitScriptUpdated);
        });
        dependencyType = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("dependencyType");
            mapper.setNewValue(defaultableConfig -> Dependency.Type.values()[defaultableConfig.getDependencyType().ordinal() - 1]);
            mapper.setValueGetter(DefaultableConfig::getDependencyType);
            mapper.setBuilder(DefaultableConfig.Builder::dependencyType);
        });
        tryGeneratingSourceJar = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("tryGeneratingSourceJar");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getTryGeneratingSourceJar());
            mapper.setValueGetter(DefaultableConfig::getTryGeneratingSourceJar);
            mapper.setBuilder(DefaultableConfig.Builder::tryGeneratingSourceJar);
        });
        tryGeneratingJavaDocJar = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("tryGeneratingJavaDocJar");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getTryGeneratingJavaDocJar());
            mapper.setValueGetter(DefaultableConfig::getTryGeneratingJavaDocJar);
            mapper.setBuilder(DefaultableConfig.Builder::tryGeneratingJavaDocJar);
        });
        enableIdeSupport = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("enableIdeSupport");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getEnableIdeSupport());
            mapper.setValueGetter(DefaultableConfig::getEnableIdeSupport);
            mapper.setBuilder(DefaultableConfig.Builder::enableIdeSupport);
        });
        registerDependencyRepositoryToProject = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("registerDependencyRepositoryToProject");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getRegisterDependencyRepositoryToProject());
            mapper.setValueGetter(DefaultableConfig::getRegisterDependencyRepositoryToProject);
            mapper.setBuilder(DefaultableConfig.Builder::registerDependencyRepositoryToProject);
        });
        generateGradleTasks = DefaultableConfigMapper.create(mapper -> {
            mapper.setName("generateGradleTasks");
            mapper.setNewValue(defaultableConfig -> !defaultableConfig.getGenerateGradleTasks());
            mapper.setValueGetter(DefaultableConfig::getGenerateGradleTasks);
            mapper.setBuilder(DefaultableConfig.Builder::generateGradleTasks);
        });
*//*        launcher = DefaultableConfigMapper.create(mapper -> {// TODO: 07/07/2023 improve
            mapper.setName("launcher");
            mapper.setValueGetter(DefaultableConfig::getLauncher);
            mapper.setBuilder((builder, obj) -> builder.buildLauncher((Closure) (Object) obj));
        });*//*
    }

    @Test
    void testMapperCount() {
        Assertions.assertEquals(DefaultableConfigFields.class.getDeclaredFields().length, mappers.size());
    }

    @TestFactory
    Stream<DynamicTest> testIndividualDefaultableConfigs() {
        return mappers.stream().
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new DefaultableTestExecutable<>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        configManager.configureDefaultable(ClosureUtil.<DefaultableConfig.Builder>configure(builder -> {
                            mapper.getBuilderConfig().accept(builder, mapper.getNewValue().apply(defaultableConfig));
                        }));

                        configManager.finalizeConfigs();
                        Assertions.assertEquals(mapper.getNewValue().apply(defaultableConfig), mapper.getValueGetter().apply(configManager.getDefaultableConfig()));
                    }
                }));
    }

    private static class DefaultableConfigMapper<T> extends ConfigMapper<DefaultableConfig, DefaultableConfig.Builder, T> {
        private static <T> DefaultableConfigMapper<T> create(Consumer<DefaultableConfigMapper<T>> config) {
            var mapper = new DefaultableConfigMapper<T>();
            config.accept(mapper);
            mappers.add(mapper);
            return mapper;
        }

        private DefaultableConfigMapper() {
        }
    }


*//*    private static final List<DefaultableConfigMapper<?>> launcherMappers = new ArrayList<>();

    private static final DefaultableConfigMapper<Boolean> keepGitUpdated;
    private static final DefaultableConfigMapper<Boolean> keepInitScriptUpdated;
    private static final DefaultableConfigMapper<Dependency.Type> dependencyType;
    private static final DefaultableConfigMapper<Boolean> tryGeneratingSourceJar;
    private static final DefaultableConfigMapper<Boolean> tryGeneratingJavaDocJar;
    private static final DefaultableConfigMapper<Boolean> enableIdeSupport;
    private static final DefaultableConfigMapper<Boolean> registerDependencyRepositoryToProject;
    private static final DefaultableConfigMapper<Boolean> generateGradleTasks;
    private static final DefaultableConfigMapper<Launchers.Launcher> launcher;

    static {


    }


    @TestFactory
    Stream<DynamicTest> testLauncherDefaultableConfigs() {
        return mappers.stream().filter(mapper -> !mapper.getName().equals(launcher.getName())).
                map(mapper -> DynamicTest.dynamicTest(mapper.getName(), new DefaultableTestExecutable<>(mapper, ProjectInstance.createProject()) {
                    @Override
                    public void execute() {
                        configManager.configureDefaultable(ClosureUtil.<DefaultableConfig.Builder>configure(builder -> {
                            mapper.getBuilderConfig().accept(builder, mapper.getNewValue().apply(defaultableConfig));
                        }));

                        configManager.configureConfigs();
                        Assertions.assertEquals(mapper.getNewValue().apply(defaultableConfig), mapper.getValueGetter().apply(configManager.getDefaultableConfig()));
                    }
                }));
    }
*//*

    private static abstract class DefaultableTestExecutable<T> implements Executable {
        DefaultableConfigMapper<T> mapper;
        LGDManagers lgdInstance;
        IConfigManager configManager;
        DefaultableConfig defaultableConfig;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public DefaultableTestExecutable(DefaultableConfigMapper mapper, Project project) {
            this.mapper = (DefaultableConfigMapper<T>) mapper;
            lgdInstance = ProjectInstance.getLGDManager(project);
            configManager = lgdInstance.getConfigManager();
            defaultableConfig = ((ConfigManager) configManager).defaultDefaultableConfig();
        }
    }*/

}
