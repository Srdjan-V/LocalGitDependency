package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class GradleInit {
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
        gradleInit.appendLine(1, "apply plugin: io.github.srdjanv.localgitdependency.injection.plugin.ModelInjectionPlugin");

        return gradleInit.render();
    }

    private final StringBuilder stringBuilder;

    public static String crateInitProject(List<Consumer<GradleInit>> listGradle) {
        return new GradleInit(listGradle).render();
    }

    private GradleInit(List<Consumer<GradleInit>> listGradle) {
        stringBuilder = new StringBuilder("rootProject {").append(System.lineSeparator());
        listGradle.forEach(gradleInitConsumer -> gradleInitConsumer.accept(this));
    }

    private GradleInit() {
        stringBuilder = new StringBuilder("initscript {").append(System.lineSeparator());
    }

    private void appendLine(int indent, String string) {
        for (int i = 0; i < indent; i++) stringBuilder.append(Constants.TAB_INDENT);
        stringBuilder.append(string).append(System.lineSeparator());
    }

    public void configureJavaJars(Consumer<List<JavaJars>> configureJavaJars) {
        List<JavaJars> javaJars = new ArrayList<>();
        configureJavaJars.accept(javaJars);

        if (javaJars.isEmpty()) return;

        appendLine(1, "java {");
        for (JavaJars javaJar : javaJars) appendLine(2, javaJar.toString());
        appendLine(1, "}");
    }

    enum JavaJars {
        SOURCES("withJavadocJar()"),
        JAVADOC("withSourcesJar()");

        private final String jars;

        JavaJars(String jars) {
            this.jars = jars;
        }

        @Override
        public String toString() {
            return jars;
        }
    }

    public void configureJarTasks(Consumer<List<JarTasks>> configureJarTasks) {
        List<JarTasks> jarTasks = new ArrayList<>();
        configureJarTasks.accept(jarTasks);

        if (jarTasks.isEmpty()) return;

        for (JarTasks jarTask : jarTasks)
            jarTask.buildJarTask(this);

        appendLine(1, "artifacts {");
        for (JarTasks jarTask : jarTasks)
            appendLine(2, String.format("archives %s", jarTask.name));
        appendLine(1, "}");
    }

    enum JarTasks {
        SOURCES("lgdSourceTask", "sourceSets.main.allJava", "sources"),
        JAVADOC("lgdDocTask", "sourceSets.main.allJava", "javadoc");

        private final String name;
        private final String sourceSets;
        private final String classifier;

        JarTasks(String name, String sourceSets, String classifier) {
            this.name = name;
            this.sourceSets = sourceSets;
            this.classifier = classifier;
        }

        public void buildJarTask(GradleInit gradleInit) {
            gradleInit.appendLine(1, String.format("tasks.register(\"%s\", Jar) {", name));
            gradleInit.appendLine(2, String.format("from %s", sourceSets));
            gradleInit.appendLine(2, String.format("classifier = '%s'", classifier));
            gradleInit.appendLine(1, "}");
        }
    }

    public String render() {
        appendLine(0, "}");
        var ret = stringBuilder.toString();
        stringBuilder.setLength(0);
        return ret;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
