package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import static org.eclipse.jgit.lib.Constants.*;

public class GitInfo {
    private final Dependency dependency;
    private final String url;
    private final String target;
    private final TargetType targetType;
    private final File dir;
    private final boolean keepGitUpdated;
    private boolean refreshed;

    public GitInfo(DependencyProperty dependencyDependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.url = dependencyDependencyProperty.getUrl();

        if (dependencyDependencyProperty.getTargetType() == null) {
            targetType = TargetType.BRANCH;
            target = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + MASTER;
        } else {
            switch (dependencyDependencyProperty.getTargetType()) {
                case COMMIT:
                    targetType = dependencyDependencyProperty.getTargetType();
                    target = dependencyDependencyProperty.getTarget();
                    break;

                case TAG:
                    targetType = dependencyDependencyProperty.getTargetType();
                    target = R_TAGS + dependencyDependencyProperty.getTarget();
                    break;

                case BRANCH:
                    targetType = TargetType.BRANCH;
                    target = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + dependencyDependencyProperty.getTarget();
                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        this.dir = Constants.concatFile.apply(dependencyDependencyProperty.getGitDir(), dependency.getName());
        this.keepGitUpdated = dependencyDependencyProperty.getKeepGitUpdated();
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getTarget() {
        return target;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    @NotNull
    public File getDir() {
        return dir;
    }

    public boolean isKeepGitUpdated() {
        return keepGitUpdated;
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
