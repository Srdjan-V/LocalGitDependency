package io.github.srdjanv.localgitdependency.util;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class FileUtil {
    private FileUtil() {
    }

    @Nullable
    public static File configureFilePath(@Nullable File defaultDir, @Nullable String dir) {
        if (dir == null) return null;
        return configureFilePath(defaultDir, new File(dir));
    }

    @Nullable
    public static File configureFilePath(@Nullable File defaultDir, @Nullable File dir) {
        if (defaultDir == null || dir == null) return null;
        if (dir.isAbsolute()) return dir;
        return new File(defaultDir, String.valueOf(dir)).toPath().normalize().toFile();
    }


    public static void checkExistsAndMkdirs(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new UncheckedIOException(
                        new IOException(String.format("%s is not a directory, delete the file and refresh gradle", file.getAbsolutePath())));
            }
            return;
        }
        if (!file.mkdirs()) {
            throw new UncheckedIOException(
                    new IOException(String.format("Unable to create directory %s", file.getAbsolutePath())));
        }
    }
}
