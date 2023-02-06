package srki2k.localgitdependency.util;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.gradle.tooling.internal.consumer.Distribution;
import srki2k.localgitdependency.depenency.Dependency;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static srki2k.localgitdependency.util.GradleUtil.GradleInit.*;

public class GradleUtil {

    public static void buildGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = null;
        try {
            connector = createGradleConnector(dependency);
            try (ProjectConnection connection = connector.connect()) {
                BuildLauncher build = connection.newBuild();
                build.withArguments("--init-script", dependency.getInitScript().getAbsolutePath());
                build.forTasks("build");
                build.run();
            }

        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    public static void publishGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = null;
        try {
            connector = createGradleConnector(dependency);
            try (ProjectConnection connection = connector.connect()) {
                BuildLauncher build = connection.newBuild();
                build.withArguments("--init-script", dependency.getInitScript().getAbsolutePath());
                build.forTasks("maven-publish");
                build.run();
            }

        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    private static DefaultGradleConnector createGradleConnector(Dependency dependency) {
        DefaultGradleConnector connector = (DefaultGradleConnector) GradleConnector.newConnector();
        connector.searchUpwards(false);
        connector.daemonMaxIdleTime(1, TimeUnit.MICROSECONDS);
        connector.forProjectDirectory(dependency.getDir());
        return connector;
    }

    // TODO: 05/02/2023
    public static void createInitScript(Dependency dependency) {
        DefaultGradleConnector connector = null;
        try {
            connector = createGradleConnector(dependency);

            try (ProjectConnection connection = connector.connect()) {
                BuildLauncher build = connection.newBuild();
            }


            Field field = DefaultGradleConnector.class.getDeclaredField("distribution");
            field.setAccessible(true);
            Distribution distribution = (Distribution) field.get(connector);
            distribution.getDisplayName();


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }


        Task task1 = new Task("initTask1", "sourceSets.main.allJava", "source");
        Task task2 = new Task("initTask2", "sourceSets.main.allJava", "javadoc");

        Publication publication1 = new Publication("publication1", task1);
        Publication publication2 = new Publication("publication2", task2);


        String tmp = crateInitFile(
                new Plugins[]{Plugins.JAVA, Plugins.MAVEN_PUBLISH},
                new JavaJars[]{JavaJars.SOURCES, JavaJars.JAVADOC},
                new Task[]{task1, task2},
                new Artifacts(task1, task2),
                new Publishing(publication1, publication2));

    }

    static class GradleInit {
        public static String crateInitFile(Plugins[] plugins, JavaJars[] javaJars, Task[] tasks, Artifacts artifact, Publishing publishing) {
            StringBuilder stringBuilder = new StringBuilder("rootProject {").append(System.lineSeparator());
            for (Plugins plugin : plugins) {
                appendLine(stringBuilder, 1, plugin.toString());
            }

            if (javaJars != null) {
                appendLine(stringBuilder, 1, "java {");
                for (JavaJars javaBlock : javaJars) {
                    appendLine(stringBuilder, 2, javaBlock.toString());
                }
                appendLine(stringBuilder, 1, "}");
            }

            if (tasks != null) {
                for (Task task : tasks) {
                    task.buildTask(stringBuilder);
                }
            }

            if (artifact != null) {
                artifact.buildArtifact(stringBuilder);
            }

            if (publishing != null) {
                publishing.buildPublishing(stringBuilder);
            }

            return stringBuilder.append("}").toString();
        }

        private final static String stringIndent = "    ";

        private static void appendLine(StringBuilder stringBuilder, int indent, String string) {
            for (int i = 0; i < indent; i++) {
                stringBuilder.append(stringIndent);
            }
            stringBuilder.append(string).append(System.lineSeparator());
        }

        enum Plugins {
            MAVEN_PUBLISH("apply plugin: \"maven-publish\""),
            JAVA("apply plugin: \"java\"");

            private final String plugin;

            Plugins(String plugin) {
                this.plugin = plugin;
            }

            @Override
            public String toString() {
                return plugin;
            }
        }

        enum JavaJars {
            JAVADOC("withJavadocJar()"),
            SOURCES("withSourcesJar()");
            private final String javaBlock;

            JavaJars(String javaBlock) {
                this.javaBlock = javaBlock;
            }

            @Override
            public String toString() {
                return javaBlock;
            }
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

            public void buildTask(StringBuilder stringBuilder) {
                appendLine(stringBuilder, 1, String.format("tasks.register(\"%s\", Jar) {", name));
                appendLine(stringBuilder, 2, String.format("from %s", sourceSets));
                appendLine(stringBuilder, 2, String.format("classifier = '%s'", classifier));
                appendLine(stringBuilder, 1, "}");
            }
        }

        static class Artifacts {
            private final Task[] tasks;

            public Artifacts(Task... tasks) {
                this.tasks = tasks;
            }

            public void buildArtifact(StringBuilder stringBuilder) {
                appendLine(stringBuilder, 1, "artifacts {");
                for (Task task : tasks) {
                    appendLine(stringBuilder, 2, String.format("archives %s", task.name));
                }
                appendLine(stringBuilder, 1, "}");
            }
        }

        static class Publication {
            private final String publicationName;
            private final Task task;

            public Publication(String publicationName, Task task) {
                this.publicationName = publicationName;
                this.task = task;
            }
        }

        static class Publishing {
            private final Publication[] publications;

            public Publishing(Publication... publications) {
                this.publications = publications;
            }

            public void buildPublishing(StringBuilder stringBuilder) {
                appendLine(stringBuilder, 1, "publishing {");
                appendLine(stringBuilder, 2, "publications {");
                for (Publication publication : publications) {
                    appendLine(stringBuilder, 3, String.format("%s(MavenPublication) {", publication.publicationName));
                    appendLine(stringBuilder, 4, "from components.java");
                    appendLine(stringBuilder, 4, String.format("archives %s", publication.task.name));
                    appendLine(stringBuilder, 3, "}");
                }
                appendLine(stringBuilder, 2, "}");
                appendLine(stringBuilder, 1, "}");
            }
        }
    }
}

