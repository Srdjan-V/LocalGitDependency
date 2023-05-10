package io.github.srdjanv.localgitdependency.cleanup;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

class CleanupManager extends ManagerBase implements ICleanupManager {

    CleanupManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void init() {
        GlobalProperty props = getPropertyManager().getGlobalProperty();

        if (!props.getAutomaticCleanup()) {
            ManagerLogger.info("Skipping cleanup");
            return;
        }

        cleanLibsDir(props.getGitDir());
        cleanMavenDir(props.getMavenDir());
        cleanDataDir(props.getPersistentDir());
    }

    private void cleanLibsDir(File libsDir) {
        iterateDirs(libsDir, (dir, dep) -> dir.equals(dep.getGitInfo().getDir()));
    }

    private void cleanMavenDir(File mavenDir) {
        iterateDirs(mavenDir, (dir, dep) -> {
            switch (dep.getDependencyType()) {
                case MavenProjectDependencyLocal:
                    return dir.equals(dep.getMavenFolder());

                case MavenProjectLocal:
                    return dir.equals(dep.getMavenFolder().getParentFile());

                default:
                    return true;
            }
        });
    }

    private void cleanDataDir(File dataDir) {
        iterateDirs(dataDir, (dir, dep) -> dir.equals(new File(dataDir, dep.getName())));
    }

    private void iterateDirs(File fileDir, BiPredicate<File, Dependency> validDir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fileDir.toPath())) {
            rootDir:
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    for (Dependency dep : getDependencyManager().getDependencies()) {
                        if (validDir.test(path.toFile(), dep)) continue rootDir;
                    }
                    ManagerLogger.info("Cleaning directory at {}", path.toAbsolutePath());
                    deleteDir(path);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void deleteDir(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.delete(file);
                } catch (AccessDeniedException exception) {
                    if (!Files.isWritable(file)) {
                        FilePremonitions.getReadPermissions().accept(file);//read only files cant be deleted
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
