package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import org.jetbrains.annotations.Nullable;

public class BuildScriptGenerator {
    private static final String GRADLE = "CustomBuild.gradle";
    private static final String PROPS = "CustomGradle.properties";
    private static final String lgdPlugin = "useLGDPlugin";
    private static final String lgdPluginVersion = "LGDVersion";

    public static <G extends BaseGenerator> void generate(DependencyWrapper wrapper, G... generators) {
        var dir = FileUtil.getLibsDir(wrapper.getProjectManager().getProject()).getAsFile();
        dir.mkdirs();
        generate(dir.toPath(), wrapper.getTestName(), generators);
    }

    private static <G extends BaseGenerator> void generate(Path path, String baseFileName, G... generators) {
        boolean hasProps = false;
        for (G generator : generators)
            if (generator.getProperties() != null) {
                hasProps = true;
                break;
            }

        if (hasProps)
            try (var writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    path.resolve(new File(baseFileName + PROPS).toPath()).toFile(), false))))) {
                for (BaseGenerator generator : generators) {
                    if (generator.properties != null) generator.properties.list(writer);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

        try (var writer = Files.newBufferedWriter(
                path.resolve(new File(baseFileName + GRADLE).toPath()),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE)) {
            for (BaseGenerator generator : generators) {
                writer.write(generator.prefix() + System.lineSeparator());
                writer.write(generator.builder.toString());
                writer.write(generator.suffix() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract static class BaseGenerator {
        private final StringBuilder builder = new StringBuilder();
        protected final Properties properties;

        @Nullable public Properties getProperties() {
            return properties;
        }

        private BaseGenerator(Properties properties) {
            this.properties = properties;
        }

        public BaseGenerator append(String s) {
            builder.append(s);
            return this;
        }

        abstract String prefix();

        abstract String suffix();
    }

    public static class Repo extends BaseGenerator {

        public Repo() {
            super(null);
        }

        @Override
        String prefix() {
            return "repositories {";
        }

        @Override
        String suffix() {
            return "}";
        }
    }

    public static class Deps extends BaseGenerator {
        public Deps() {
            super(null);
        }

        @Override
        String prefix() {
            return "dependencies {";
        }

        @Override
        String suffix() {
            return "}";
        }
    }

    public static class LDGDeps extends BaseGenerator {

        public LDGDeps() {
            super(new Properties());
            properties.put(lgdPlugin, Boolean.toString(true));
            properties.put(lgdPluginVersion, Constants.PLUGIN_VERSION);
        }

        public void LGDVersion(String version) {
            properties.put(lgdPluginVersion, version);
        }

        @Override
        String prefix() {
            return "lgd {";
        }

        @Override
        String suffix() {
            return "}";
        }
    }

    private BuildScriptGenerator() {}
}
