package io.github.srdjanv.localgitdependency.cleanup;

import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

final class CleanupManager extends ManagerBase implements ICleanupManager {

    CleanupManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {}

    @Override
    public boolean init() {
        PluginConfig props = getConfigManager().getPluginConfig();

        if (!props.getAutomaticCleanup().get()) return false;
        return cleanLibsDir(props.getLibsDir().getAsFile().get())
                || cleanDataDir(FileUtil.getLgdDataDir(getProject()).getAsFile());
    }

    private boolean cleanLibsDir(File libsDir) {
        if (!libsDir.exists()) return false;
        return iterateDirs(
                libsDir, true, (dir, dep) -> dir.equals(dep.getGitInfo().getDir()));
    }

    private boolean cleanDataDir(File dataDir) {
        if (!dataDir.exists()) return false;
        return iterateDirs(dataDir, false, (dir, dep) -> dep.getName().equals(dir.getName()));
    }

    private boolean iterateDirs(File fileDir, boolean log, BiPredicate<File, Dependency> validDir) {
        boolean didWork = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fileDir.toPath())) {
            rootDir:
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    for (Dependency dep : getDependencyManager().getDependencies()) {
                        if (validDir.test(path.toFile(), dep)) continue rootDir;
                    }
                    if (log) ManagerLogger.info("Cleaning directory at {}", path.toAbsolutePath());
                    deleteDir(path);
                    didWork = true;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return didWork;
    }

    private void deleteDir(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.delete(file);
                } catch (AccessDeniedException exception) {
                    if (!Files.isWritable(file)) {
                        FilePremonitions.getReadPermissions().accept(file); // read only files cant be deleted
                        Files.delete(file);
                    } else {
                        throw exception;
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
