package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class GradleInit {
    public static String createInitProbe() {
        GradleInit gradleInit = new GradleInit();
        gradleInit.appendLine(1, "repositories {");
        gradleInit.appendLine(2, "gradlePluginPortal()");
        gradleInit.appendLine(2, "mavenCentral()");
        gradleInit.appendLine(2, "mavenLocal()");
        gradleInit.appendLine(1, "}");

        gradleInit.appendLine(1, "dependencies {");
        gradleInit.appendLine(2, String.format("classpath \"io.github.srdjan-v:local-git-dependency:%s\"", Constants.PLUGIN_VERSION));
        gradleInit.appendLine(1, "}");
        gradleInit.appendLine(0, "}");

        gradleInit.appendLine(0, "rootProject {");
        gradleInit.appendLine(1, Plugins.modelInjection());

        return gradleInit.render();
    }

    private final StringBuilder stringBuilder;
    private List<Task> mavenTasks;


    public static String crateInitProject(List<Consumer<GradleInit>> listGradle) {
        return new GradleInit(listGradle).render();
    }

    private GradleInit(List<Consumer<GradleInit>> listGradle) {
        mavenTasks = new ArrayList<>();
        stringBuilder = new StringBuilder("rootProject {").append(System.lineSeparator());
        listGradle.forEach(gradleInitConsumer -> gradleInitConsumer.accept(this));
    }

    private GradleInit() {
        stringBuilder = new StringBuilder("initscript {").append(System.lineSeparator());
    }

    private void appendLine(int indent, String string) {
        for (int i = 0; i < indent; i++) {
            stringBuilder.append(Constants.TAB_INDENT);
        }
        stringBuilder.append(string).append(System.lineSeparator());
    }

    public void setPlugins(Consumer<List<String>> plugins) {
        List<String> pluginsList = new ArrayList<>();
        plugins.accept(pluginsList);

        for (String plugin : pluginsList) {
            appendLine(1, plugin);
        }
    }

    static class Plugins {
        private static final String mavenPublish = "apply plugin: \"maven-publish\"";
        private static final String java = "apply plugin: \"java\"";
        private static final String modelInjection = "apply plugin: io.github.srdjanv.localgitdependency.injection.plugin.ModelInjectionPlugin";

        private Plugins() {
        }

        public static String mavenPublish() {
            return mavenPublish;
        }

        public static String java() {
            return java;
        }

        public static String modelInjection() {
            return modelInjection;
        }

    }

    public void setJavaJars(Consumer<List<JavaJars>> javaJars) {
        List<JavaJars> javaJarsList = new ArrayList<>();
        javaJars.accept(javaJarsList);

        if (javaJarsList.isEmpty()) return;

        appendLine(1, "java {");
        for (JavaJars javaJar : javaJarsList) {
            appendLine(2, javaJar.toString());
        }
        appendLine(1, "}");
    }

    static class JavaJars {
        private final String jars;

        private JavaJars(String jars) {
            this.jars = jars;
        }

        public static JavaJars javadoc() {
            return new JavaJars("withJavadocJar()");
        }

        public static JavaJars sources() {
            return new JavaJars("withSourcesJar()");
        }

        @Override
        public String toString() {
            return jars;
        }
    }

    public void setTasks(Consumer<List<Task>> Tasks) {
        List<Task> tasks = new ArrayList<>();
        Tasks.accept(tasks);

        if (tasks.isEmpty()) return;

        for (Task task : tasks) {
            if (task.mavenTask) mavenTasks.add(task);
            task.buildTask(this);
        }

        appendLine(1, "artifacts {");
        for (Task task : tasks) {
            appendLine(2, String.format("archives %s", task.name));
        }
        appendLine(1, "}");
    }

    static class Task {
        private final String name;
        private final String sourceSets;
        private final String classifier;
        private final boolean mavenTask;

        public Task(String name, String sourceSets, String classifier, boolean mavenTask) {
            this.name = name;
            this.sourceSets = sourceSets;
            this.classifier = classifier;
            this.mavenTask = mavenTask;
        }

        public void buildTask(GradleInit gradleInit) {
            gradleInit.appendLine(1, String.format("tasks.register(\"%s\", Jar) {", name));
            gradleInit.appendLine(2, String.format("from %s", sourceSets));
            gradleInit.appendLine(2, String.format("classifier = '%s'", classifier));
            gradleInit.appendLine(1, "}");
        }
    }

    public void setPublishing(Consumer<List<Publication>> Publication) {
        List<Publication> publications = new ArrayList<>();
        Publication.accept(publications);

        if (publications.isEmpty()) return;

        appendLine(1, "publishing {");
        appendLine(2, "publications {");
        for (Publication publication : publications) {
            appendLine(3, String.format("%s(MavenPublication) {", publication.publicationName));
            appendLine(4, "from components.java");
            for (Task task : mavenTasks) {
                appendLine(4, String.format("artifact %s", task.name));
            }
            appendLine(3, "}");
        }
        appendLine(2, "}");

        appendLine(2, "repositories {");
        appendLine(3, "maven {");
        for (Publication publication : publications) {
            appendLine(4, String.format("name '%s'", publication.repositoryName));
            if (publication.mavenLocalFolder != null)
                appendLine(4, String.format("url \"file://%s\"", publication.mavenLocalFolder.getAbsolutePath().replace("\\", "\\\\")));
        }
        appendLine(3, "}");
        appendLine(2, "}");

        appendLine(1, "}");
    }

    static class Publication {
        private final String repositoryName;
        private final File mavenLocalFolder;
        private final String publicationName;

        public Publication(String repositoryName, File mavenLocalFolder, String publicationName) {
            this.repositoryName = repositoryName;
            this.mavenLocalFolder = mavenLocalFolder;
            this.publicationName = publicationName;
        }
    }

    public String render() {
        appendLine(0, "}");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
