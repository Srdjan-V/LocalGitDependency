package io.github.srdjanv.localgitdependency.util;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
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
            throw new UncheckedIOException(new InvalidObjectException(String.format(
                    "Invalid data for method: %s, acceptable types are JavaLauncher, RegularFile, File, Path, String and Property, Provider of anny of these types",
                    methodName)));
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
}
