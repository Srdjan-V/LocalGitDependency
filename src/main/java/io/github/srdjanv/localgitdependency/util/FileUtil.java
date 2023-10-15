package io.github.srdjanv.localgitdependency.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.jetbrains.annotations.Nullable;

public final class FileUtil {
    private FileUtil() {}

    @Nullable public static File configureFilePath(@Nullable File defaultDir, @Nullable File dir) {
        if (dir == null) return null;
        if (dir.isAbsolute()) return dir;
        if (defaultDir == null) return null;
        return new File(defaultDir, String.valueOf(dir)).toPath().normalize().toFile();
    }

    public static File toFile(Object object, String methodName) {
        if (object instanceof File file) {
            return file;
        } else if (object instanceof RegularFile file) {
            return file.getAsFile();
        } else if (object instanceof Path file) {
            return file.toFile();
        } else if (object instanceof String file) {
            return new File(file);
        } else if (object instanceof Property property) {
            return toFile(property.get(), methodName);
        } else if (object instanceof Provider provider) {
            return toFile(provider.get(), methodName);
        } else if (object instanceof JavaLauncher javaLauncher) {
            return javaLauncher.getMetadata().getInstallationPath().getAsFile();
        } else {
            throw new InvalidUserDataException(String.format(
                    "Invalid data for method: %s, acceptable types are JavaLauncher, RegularFile, File, Path, String and Property, Provider of anny of these types",
                    methodName));
        }
    }

    public static void writeToFile(File file, String text) throws IOException {
        try (BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            bufferedOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void checkExistsAndMkdirs(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new UncheckedIOException(new IOException(String.format(
                        "%s is not a directory, delete the file and refresh gradle", file.getAbsolutePath())));
            }
            return;
        }
        if (!file.mkdirs()) {
            throw new UncheckedIOException(
                    new IOException(String.format("Unable to create directory %s", file.getAbsolutePath())));
        }
    }

    public static File concat(File root, String path) {
        return new File(root, path);
    }

    public static File toBuildDir(File file) {
        return new File(file, "/build/libs");
    }

    // Dependency data file generators
    public static File getPersistentInitScript(File persistentFolder, String name) {
        File persistentInitScript = new File(persistentFolder, name + "/" + name + "Init.gradle");
        checkExistsAndMkdirs(persistentInitScript.getParentFile());
        return persistentInitScript;
    }

    public static File getPersistentJsonFile(File persistentFolder, String name) {
        File persistentJsonFile = new File(persistentFolder, name + "/" + name + ".json");
        checkExistsAndMkdirs(persistentJsonFile.getParentFile());
        return persistentJsonFile;
    }

    // Default plugin dirs
    public static Directory getLibsDir(Project project) {
        return project.getLayout().getProjectDirectory().dir("/libs");
    }

    public static Directory getLgdDir(Project project) {
        return project.getLayout().getBuildDirectory().dir("/lgd").get();
    }

    public static Directory getLgdDataDir(Project project) {
        return project.getLayout().getBuildDirectory().dir("/lgd/data").get();
    }
}
