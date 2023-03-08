package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.CommonPropertyBuilder;
import io.github.srdjanv.localgitdependency.property.CommonPropertyGetters;
import io.github.srdjanv.localgitdependency.property.DefaultProperty;
import io.github.srdjanv.localgitdependency.util.dsl.BaseMapper;
import io.github.srdjanv.localgitdependency.util.dsl.GlobalToGlobalMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DslTests {
    private static List<BaseMapper> globalToGlobalDslMappers;

    private void initGlobalToGlobalMappers() {
        if (globalToGlobalDslMappers != null) return;
        globalToGlobalDslMappers = new ArrayList<>();
        BiConsumer<Object, Object> fileAssertion = (expected, actual) -> {
            if (expected instanceof String) {
                String expectedFile = ((String) expected).replace("\\", "/");
                String actualFile = ((File) actual).getAbsolutePath().replace("\\", "/");

                Assertions.assertTrue(actualFile.contains(expectedFile));
                return;
            }

            File expectedFile = (File) expected;
            File actualFile = (File) actual;

            Assertions.assertEquals(expectedFile.getName(), actualFile.getName());
        };

        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(DefaultProperty.class, "getKeepMainInitScriptUpdated"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> !(Boolean) o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.keepMainInitScriptUpdated((Boolean) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getConfiguration"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> "newTest" + o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.configuration((String) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getKeepGitUpdated"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> !(Boolean) o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.keepGitUpdated((Boolean) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getDir"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> new File("/CustomFileTest/", ((File) o).getName()));
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.gitDir((File) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getDir"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> "/CustomStringTestGitDir");
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.gitDir((String) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getJavaHomeDir"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> new File("/CustomFileTest/"));
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.javaHomeDir((File) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getJavaHomeDir"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> "/CustomStringTestJavaDir");
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.javaHomeDir((String) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getPersistentFolder"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> new File("/CustomFileTest/", ((File) o).getName()));
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.persistentFolder((File) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getPersistentFolder"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> "/CustomStringTestPersistentFolder");
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.persistentFolder((String) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getMavenFolder"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> new File("/CustomFileTest/", ((File) o).getName()));
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.mavenFolder((File) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getMavenFolder"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> "/CustomStringTestMavenFolder");
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.mavenFolder((String) o));
            globalToGlobalMapper.setAssertion(fileAssertion);
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getDependencyType"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> {
                for (Dependency.Type type : Dependency.Type.values()) {
                    if (type != o) {
                        return type;
                    }
                }
                throw new IllegalStateException();
            });
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.dependencyType((Dependency.Type) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getKeepDependencyInitScriptUpdated"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> !(Boolean) o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.keepDependencyInitScriptUpdated((Boolean) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getTryGeneratingSourceJar"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> !(Boolean) o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.tryGeneratingSourceJar((Boolean) o));
        }));
        globalToGlobalDslMappers.add(GlobalToGlobalMapper.configure(globalToGlobalMapper -> {
            globalToGlobalMapper.getOldGlobalConfig(reflectMethod(CommonPropertyGetters.class, "getTryGeneratingJavaDocJar"));
            globalToGlobalMapper.resolveNewGlobalConfig(o -> !(Boolean) o);
            globalToGlobalMapper.setNewGlobalConfig((o, builder) -> builder.tryGeneratingJavaDocJar((Boolean) o));
        }));

        Assertions.assertEquals(DefaultProperty.Builder.class.getDeclaredMethods().length
                + CommonPropertyBuilder.class.getDeclaredMethods().length - 1, globalToGlobalDslMappers.size());
    }

    private Method reflectMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(method, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @TestFactory
    Stream<DynamicTest> GlobalToGlobalConfigurationBlockTest() {
        initGlobalToGlobalMappers();
        return globalToGlobalDslMappers.stream().map(baseMapper ->
                DynamicTest.dynamicTest(baseMapper.getTestName(), baseMapper::runTest));
    }

}
