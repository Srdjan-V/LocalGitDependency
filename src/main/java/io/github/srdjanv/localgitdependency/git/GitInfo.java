package io.github.srdjanv.localgitdependency.git;

import static org.eclipse.jgit.lib.Constants.*;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class GitInfo {
    private final Dependency dependency;
    private final String url;
    private final String target;
    private final String targetLocal;
    private final String targetRemote;
    private final TargetType targetType;
    private final File dir;
    private final boolean keepGitUpdated;
    private final boolean forceGitUpdate;
    private final boolean cloneGitSubmodules;
    private boolean refreshed;

    public GitInfo(Managers managers, DependencyConfig dependencyConfig, Dependency dependency) {
        this.dependency = dependency;
        this.url = dependencyConfig.getUrl().get();

        if (dependencyConfig.getCommit().isPresent()) {
            targetType = TargetType.COMMIT;
            target = dependencyConfig.getCommit().get();
            targetLocal = target;
            targetRemote = target;
        } else if (dependencyConfig.getTag().isPresent()) {
            targetType = TargetType.TAG;
            target = dependencyConfig.getTag().get();
            targetLocal = R_TAGS + target;
            targetRemote = R_TAGS + target;
        } else if (dependencyConfig.getBranch().isPresent()) {
            targetType = TargetType.BRANCH;
            target = dependencyConfig.getBranch().get();
            targetLocal = R_HEADS + target;
            targetRemote = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + target;
        } else {
            targetType = TargetType.BRANCH;
            target = MASTER;
            targetLocal = R_HEADS + target;
            targetRemote = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + target;
        }

        final File dir;
        if (dependencyConfig.getLibsDir().isPresent()) {
            dir = FileUtil.toFile(dependencyConfig.getLibsDir().get(), "getLibsDir");
        } else {
            dir = managers.getConfigManager()
                    .getPluginConfig()
                    .getLibsDir()
                    .get()
                    .getAsFile();
        }
        this.dir = FileUtil.concat(dir, dependency.getName());

        this.keepGitUpdated = dependencyConfig.getKeepGitUpdated().get();
        this.forceGitUpdate = dependencyConfig.getForceGitUpdate().get();
        this.cloneGitSubmodules = dependencyConfig.getCloneGitSubmodules().get();
    }

    @NotNull public Dependency getDependency() {
        return dependency;
    }

    @NotNull public String getUrl() {
        return url;
    }

    @NotNull public String getTarget() {
        return target;
    }

    @NotNull public String getTargetLocal() {
        return targetLocal;
    }

    @NotNull public String getTargetRemote() {
        return targetRemote;
    }

    @NotNull public TargetType getTargetType() {
        return targetType;
    }

    @NotNull public File getDir() {
        return dir;
    }

    public boolean isKeepGitUpdated() {
        return keepGitUpdated;
    }

    public boolean isForceGitUpdate() {
        return forceGitUpdate;
    }

    public boolean isCloneGitSubmodules() {
        return cloneGitSubmodules;
    }

    public boolean hasRefreshed() {
        return refreshed;
    }

    public void setRefreshed() {
        refreshed = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitInfo gitInfo = (GitInfo) o;
        return Objects.equals(gitInfo.getDependency().getName(), getDependency().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency.getName());
    }

    public enum TargetType {
        COMMIT,
        BRANCH,
        TAG
    }
}
