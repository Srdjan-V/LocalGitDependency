package srki2k.localgitdependency.gradle;

public class GradleInit {
    private GradleInit() {
    }

    public static String createInitProbe() {
        StringBuilder stringBuilder = new StringBuilder("initscript {").append(System.lineSeparator());

        appendLine(stringBuilder, 1, "repositories {");
        appendLine(stringBuilder, 2, "mavenCentral()");
        appendLine(stringBuilder, 2, "mavenLocal()");
        appendLine(stringBuilder, 1, "}");

        appendLine(stringBuilder, 1, "dependencies {");
        appendLine(stringBuilder, 2, "classpath \"srki2k:local-git-dependency:0.+\""); // TODO: 07/02/2023 make the version dynamic
        appendLine(stringBuilder, 1, "}");
        appendLine(stringBuilder, 0, "}");

        appendLine(stringBuilder, 0, "rootProject {");
        appendLine(stringBuilder, 1, Plugins.MODEL_INJECTION.toString());
        appendLine(stringBuilder, 0, "}");

        return stringBuilder.toString();
    }

    public static String crateInitProject(Plugins[] plugins, JavaJars[] javaJars, Task[] tasks, Artifacts artifact, Publishing publishing) {
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
        JAVA("apply plugin: \"java\""),
        MODEL_INJECTION("apply plugin: srki2k.localgitdependency.injection.plugin.ModelInjectionPlugin");

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
                if (publication.task != null)
                    appendLine(stringBuilder, 4, String.format("artifact %s", publication.task.name));
                appendLine(stringBuilder, 3, "}");
            }
            appendLine(stringBuilder, 2, "}");
            appendLine(stringBuilder, 1, "}");
        }
    }
}
