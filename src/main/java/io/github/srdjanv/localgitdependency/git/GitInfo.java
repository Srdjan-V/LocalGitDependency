package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.impl.Property;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.eclipse.jgit.lib.Constants.*;

public class GitInfo {
    private final Dependency dependency;
    private final String url;
    private final String target;
    private final TargetType targetType;
    private final File dir;
    private final boolean keepGitUpdated;
    private boolean refreshed;

    public GitInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.url = dependencyProperty.getUrl();

        if (dependencyProperty.getTargetType() == null) {
            targetType = TargetType.BRANCH;
            target = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + MASTER;
        } else {
            switch (dependencyProperty.getTargetType()) {
                case COMMIT:
                    targetType = dependencyProperty.getTargetType();
                    target = dependencyProperty.getTarget();
                    break;

                case TAG:
                    targetType = dependencyProperty.getTargetType();
                    target = R_TAGS + dependencyProperty.getTarget();
                    break;

                case BRANCH:
                    targetType = TargetType.BRANCH;
                    target = R_REMOTES + DEFAULT_REMOTE_NAME + "/" + dependencyProperty.getTarget();
                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        this.dir = Constants.concatFile.apply(dependencyProperty.getGitDir(), dependency.getName());
        this.keepGitUpdated = dependencyProperty.getKeepGitUpdated();
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

    public enum TargetType {
        COMMIT,
        BRANCH,
        TAG
    }
}
