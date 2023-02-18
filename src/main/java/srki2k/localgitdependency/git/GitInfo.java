package srki2k.localgitdependency.git;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.property.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.Constants.MASTER;

public class GitInfo {
    private final Dependency dependency;
    private final String url;
    private final String commit;
    private final File dir;
    private final boolean keepGitUpdated;
    private List<Exception> gitExceptions;
    private boolean refreshed;

    public GitInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.url = dependencyProperty.getUrl();
        this.commit = dependencyProperty.getCommit() == null ? DEFAULT_REMOTE_NAME + "/" + MASTER : dependencyProperty.getCommit(); // TODO: 18/02/2023 improve
        this.dir = Constants.concatFile.apply(dependencyProperty.getDir(), dependency.getName());
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
    public String getCommit() {
        return commit;
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

    public boolean hasGitExceptions() {
        return gitExceptions != null && !gitExceptions.isEmpty();
    }

    @Nullable
    public List<Exception> getGitExceptions() {
        return gitExceptions;
    }

    public void addGitExceptions(Exception gitException) {
        this.gitExceptions = createList(this.gitExceptions);
        this.gitExceptions.add(gitException);
    }

    private static List<Exception> createList(List<Exception> o) {
        if (o == null) {
            return new ArrayList<>();
        }
        return o;
    }

}
