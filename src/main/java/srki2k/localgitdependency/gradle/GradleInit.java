package srki2k.localgitdependency.gradle;

import srki2k.localgitdependency.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GradleInit {
    public static String createInitProbe() {
        GradleInit gradleInit = new GradleInit();
        gradleInit.appendLine(1, "repositories {");
        gradleInit.appendLine(2, "mavenCentral()");
        gradleInit.appendLine(2, "mavenLocal()");
        gradleInit.appendLine(1, "}");

        gradleInit.appendLine(1, "dependencies {");
        gradleInit.appendLine(2, String.format("classpath \"srki2k:local-git-dependency:%s\"", Constants.PROJECT_VERSION));
        gradleInit.appendLine(1, "}");
        gradleInit.appendLine(0, "}");

        gradleInit.appendLine(0, "rootProject {");
        gradleInit.appendLine(1, Plugins.modelInjection().toString());

        return gradleInit.render();
    }

    private final StringBuilder stringBuilder;

    public static String crateInitProject(Consumer<GradleInit> gradle) {
        return new GradleInit(gradle).render();
    }

    private GradleInit(Consumer<GradleInit> gradle) {
        stringBuilder = new StringBuilder("rootProject {").append(System.lineSeparator());
        gradle.accept(this);
    }

    private GradleInit() {
        stringBuilder = new StringBuilder("initscript {").append(System.lineSeparator());
    }

    private void appendLine(int indent, String string) {
        for (int i = 0; i < indent; i++) {
            stringBuilder.append("    ");
        }
        stringBuilder.append(string).append(System.lineSeparator());
    }

    public void setPlugins(Consumer<List<Plugins>> plugins) {
        List<Plugins> pluginsList = new ArrayList<>();
        plugins.accept(pluginsList);

        for (Plugins plugin : pluginsList) {
            appendLine(1, plugin.toString());
        }
    }

    static class Plugins {
        private final String plugin;

        private Plugins(String plugin) {
            this.plugin = plugin;
        }

        public static Plugins mavenPublish() {
            return new Plugins("apply plugin: \"maven-publish\"");
        }

        public static Plugins java() {
            return new Plugins("apply plugin: \"java\"");
        }

        public static Plugins modelInjection() {
            return new Plugins("apply plugin: srki2k.localgitdependency.injection.plugin.ModelInjectionPlugin");
        }

        @Override
        public String toString() {
            return plugin;
        }
    }

    public void setJavaJars(Consumer<List<JavaJars>> javaJars) {
        List<JavaJars> javaJarsList = new ArrayList<>();
        javaJars.accept(javaJarsList);

        appendLine(1, "java {");
        for (JavaJars javaJar : javaJarsList) {
            appendLine(1, javaJar.toString());
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

        for (Task task : tasks) {
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

        public Task(String name, String sourceSets, String classifier) {
            this.name = name;
            this.sourceSets = sourceSets;
            this.classifier = classifier;
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

        appendLine(1, "publishing {");
        appendLine(2, "publications {");
        for (Publication publication : publications) {
            appendLine(3, String.format("%s(MavenPublication) {", publication.publicationName));
            appendLine(4, "from components.java");
            if (publication.tasks != null) {
                for (Task task : publication.tasks) {
                    appendLine(4, String.format("artifact %s", task.name));
                }
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
        private final List<Task> tasks;

        public Publication(String repositoryName, File mavenLocalFolder, String publicationName, List<Task> tasks) {
            this.repositoryName = repositoryName;
            this.mavenLocalFolder = mavenLocalFolder;
            this.publicationName = publicationName;
            this.tasks = tasks;
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
