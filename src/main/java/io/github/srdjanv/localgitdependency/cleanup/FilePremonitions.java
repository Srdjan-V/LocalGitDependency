package io.github.srdjanv.localgitdependency.cleanup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.function.Consumer;

class FilePremonitions {
    private FilePremonitions() {}

    private static Consumer<Path> readPermissions;

    public static Consumer<Path> getReadPermissions() {
        if (readPermissions == null) {
            createReadPermissions();
        }

        return readPermissions;
    }

    private static void createReadPermissions() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            readPermissions = p -> {
                try {
                    Files.setAttribute(p, "dos:readonly", false);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        } else {
            readPermissions = p -> {
                try {
                    Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p);
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    Files.setPosixFilePermissions(p, perms);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        }
    }
}
