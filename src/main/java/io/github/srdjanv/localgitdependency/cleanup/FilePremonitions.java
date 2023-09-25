package io.github.srdjanv.localgitdependency.cleanup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.gradle.internal.impldep.org.apache.commons.io.function.IOConsumer;

class FilePremonitions {
    private static final IOConsumer<Path> readPermissions;

    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            readPermissions = path -> Files.setAttribute(path, "dos:readonly", false);
        } else
            readPermissions = path -> {
                Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
                perms.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, perms);
            };
    }

    static void changeReadPermissions(Path path) throws IOException {
        readPermissions.accept(path);
    }

    @FunctionalInterface
    interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    private FilePremonitions() {}
}
