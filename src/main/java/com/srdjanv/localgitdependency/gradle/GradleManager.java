package com.srdjanv.localgitdependency.gradle;

import com.srdjanv.localgitdependency.persistence.SerializableProperty;
import org.eclipse.jgit.util.sha1.SHA1;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import com.srdjanv.localgitdependency.property.DefaultProperty;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GradleManager {
    private final Map<File, DefaultGradleConnector> gradleConnectorCache = new HashMap<>();

    public void disconnectAllGradleConnectors() {
        gradleConnectorCache.values().forEach(DefaultGradleConnector::disconnect);
    }

    private DefaultGradleConnector getGradleConnector(Dependency dependency) {
        DefaultGradleConnector gradleConnector = gradleConnectorCache.get(dependency.getGitInfo().getDir());
        if (gradleConnector == null) {
            gradleConnector = (DefaultGradleConnector) GradleConnector.newConnector();
            gradleConnector.searchUpwards(false);
            gradleConnector.daemonMaxIdleTime(1, TimeUnit.MICROSECONDS);
            gradleConnector.forProjectDirectory(dependency.getGitInfo().getDir());
            gradleConnectorCache.put(dependency.getGitInfo().getDir(), gradleConnector);
        }
        return gradleConnector;
    }

    public void initGradleAPI() {
        validateMainInitScript();
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (!dependency.getPersistentInfo().isValidModel()) {
                probeProject(dependency);
            }

            validateDependencyInitScript(dependency);
        }
    }

    public void buildDependencies() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (dependency.getGitInfo().hasRefreshed() || dependency.getPersistentInfo().hasDependencyTypeChanged()) {
                buildDependency(dependency);
            }
        }
    }

    public void buildDependency(Dependency dependency) {
        switch (dependency.getDependencyType()) {
            case Jar:
            case JarFlatDir:
                buildGradleProject(dependency);
                break;

            case MavenLocal:
                publishGradleProject(dependency,
                        Constants.PublicationTaskName.apply(
                                dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject().getPublicationName()));
                break;

            case MavenProjectDependencyLocal:
            case MavenProjectLocal:
                SerializableProperty.PublicationObjectSerializable publicationObjectSerializable = dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject();
                publishGradleProject(dependency,
                        Constants.FilePublicationTaskName.apply(
                                publicationObjectSerializable.getPublicationName(),
                                publicationObjectSerializable.getRepositoryName()));
                break;
        }
    }

    private void probeProject(Dependency dependency) {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyInfoModel> customModelBuilder = connection.model(LocalGitDependencyInfoModel.class);
            customModelBuilder.withArguments("--init-script", mainInit.getAbsolutePath());
            LocalGitDependencyInfoModel localGitDependencyInfoModel = customModelBuilder.get();
            dependency.getPersistentInfo().setDefaultLocalGitDependencyInfoModel(localGitDependencyInfoModel);
        }
    }

    private void buildGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getGradleInfo().getInitScript().getAbsolutePath());
            build.forTasks("build");
            build.run();
        }
    }

    private void publishGradleProject(Dependency dependency, String task) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getGradleInfo().getInitScript().getAbsolutePath());
            build.forTasks(task);
            build.run();
        }
    }

    private String createDependencyInitScript(Dependency dependency) {
        // TODO: 18/02/2023 work on this
        SerializableProperty.DependencyInfoModelSerializable model = dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel();
        int[] gradleVersion = Arrays.stream(model.getProjectGradleVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();

        Consumer<List<GradleInit.Plugins>> plugins = pluginsList -> {
            pluginsList.add(GradleInit.Plugins.java());
            pluginsList.add(GradleInit.Plugins.mavenPublish());
        };

        if (gradleVersion[0] >= 6 && gradleVersion[1] >= 0) {
            Consumer<GradleInit> configuration = gradleInit -> {
                gradleInit.setPlugins(plugins);
                gradleInit.setJavaJars(javaJars -> {
                    if (dependency.getGradleInfo().isTryGeneratingSourceJar() &&
                            dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().isCanProjectUseWithSourcesJar()) {
                        javaJars.add(GradleInit.JavaJars.sources());
                    }
                    if (dependency.getGradleInfo().isTryGeneratingJavaDocJar() &&
                            dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().isCanProjectUseWithJavadocJar()) {
                        javaJars.add(GradleInit.JavaJars.javadoc());
                    }
                });

                gradleInit.setPublishing(taskPublication -> taskPublication.add(new GradleInit.Publication(
                        dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject().getRepositoryName(),
                        dependency.getMavenFolder(),
                        dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject().getPublicationName(),
                        null)));
            };
            return GradleInit.crateInitProject(configuration);
        }

        SerializableProperty.PublicationObjectSerializable publicationObject = dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject();
        List<GradleInit.Task> tasks = new ArrayList<>();
        for (SerializableProperty.TaskObjectSerializable taskSerializable : publicationObject.getTasks()) {
            switch (taskSerializable.getClassifier()) {
                case "sources":
                    if (dependency.getGradleInfo().isTryGeneratingSourceJar()) {
                        tasks.add(new GradleInit.Task(taskSerializable.getName(), "sourceSets.main.allJava", taskSerializable.getClassifier()));
                    }
                    break;

                case "javadoc":
                    if (dependency.getGradleInfo().isTryGeneratingJavaDocJar()) {
                        tasks.add(new GradleInit.Task(taskSerializable.getName(), "sourceSets.main.allJava", taskSerializable.getClassifier()));
                    }
            }
        }

        Consumer<GradleInit> configuration = gradleInit -> {
            gradleInit.setPlugins(plugins);
            gradleInit.setTasks(t -> t.addAll(tasks));
            gradleInit.setPublishing(p -> p.add(new GradleInit.Publication(
                    dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject().getRepositoryName(),
                    dependency.getMavenFolder(),
                    dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getPublicationObject().getPublicationName(),
                    tasks)));
        };
        return GradleInit.crateInitProject(configuration);
    }

    private void validateDependencyInitScript(Dependency dependency) {
        validateScript(
                dependency.getGradleInfo().getInitScript(),
                dependency.getGradleInfo().isKeepDependencyInitScriptUpdated(),
                () -> createDependencyInitScript(dependency),
                dependency.getPersistentInfo()::getInitFileSHA1,
                dependency.getPersistentInfo()::setInitFileSHA1);
    }

    private void validateMainInitScript() {
        DefaultProperty globalProperty = Instances.getPropertyManager().getGlobalProperty();
        File mainInit = Constants.concatFile.apply(globalProperty.getPersistentFolder(), Constants.MAIN_INIT_SCRIPT_GRADLE);
        validateScript(
                mainInit,
                globalProperty.getKeepMainInitScriptUpdated(),
                GradleInit::createInitProbe,
                Instances.getPersistenceManager()::getInitScriptSHA,
                Instances.getPersistenceManager()::setInitScriptSHA);
    }

    private void validateScript(File file, boolean keepUpdated, Supplier<String> scriptSupplier, Supplier<String> persistentSHASupplier, Consumer<String> persistentSHASetter) {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new RuntimeException(String.format("This path: '%s' leads to a folder, it must be a file", file.getAbsolutePath()));
            }

            if (keepUpdated) {
                final String fileInitScriptSHA = generateShaForFile(file);
                final String persistentInitScriptSHA = persistentSHASupplier.get();

                if (!fileInitScriptSHA.equals(persistentInitScriptSHA)) {
                    Logger.info("File {}, contains local changes, updating file", file.getName());
                    final String initScript = scriptSupplier.get();
                    writeToFile(file, initScript);
                    persistentSHASetter.accept(generateShaForString(initScript));
                    return;
                }

                final String initScript = scriptSupplier.get();
                final String targetInitScriptSHA = generateShaForString(initScript);

                if (!fileInitScriptSHA.equals(targetInitScriptSHA)) {
                    Logger.info("Updating file {}", file.getName());
                    writeToFile(file, initScript);
                    persistentSHASetter.accept(targetInitScriptSHA);
                }
            }
        } else {// TODO: 03/03/2023 create file here
            Logger.info("Creating {}", file.getName());
            final String initScript = scriptSupplier.get();
            persistentSHASetter.accept(generateShaForString(initScript));
            writeToFile(file, initScript);
        }
    }


    private String generateShaForFile(File file) {
        SHA1 sha1 = SHA1.newInstance();
        byte[] buffer = new byte[4096];
        int read;

        try (FileInputStream inputStream = new FileInputStream(file)) {
            while ((read = inputStream.read(buffer)) > 0) {
                sha1.update(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while checking %s file integrity", file.getName()), e);
        }

        return sha1.toObjectId().getName();
    }

    private String generateShaForString(String script) {
        SHA1 sha1 = SHA1.newInstance();
        sha1.update(script.getBytes(StandardCharsets.UTF_8));
        return sha1.toObjectId().getName();
    }

    private void writeToFile(File file, String text) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            bufferedOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

