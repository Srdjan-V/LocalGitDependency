package srki2k.localgitdependency.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.Logger;

import java.io.IOException;
import java.util.Objects;

// TODO: 10/02/2023 test
// Some code has been taken from
// https://github.com/alexvasilkov/GradleGitDependenciesPlugin/blob/master/src/main/groovy/com/alexvasilkov/gradle/git/utils/GitUtils.groovy
class GitObjectWrapper implements AutoCloseable, GitTasks {
    private boolean gitExceptions;
    private Git git;
    private Dependency dependency;


    GitObjectWrapper(Dependency dependency) {
        this.dependency = dependency;
        try {
            git = Git.open(srki2k.localgitdependency.Constants.concatFile.apply(dependency.getDir(), Constants.DOT_GIT));
        } catch (RepositoryNotFoundException initRepo) {
            cloneRepo();
        } catch (Exception exception) {
            dependency.addGitExceptions(exception);
        }
    }

    @Override
    public void setup() {
        final String remoteUrl = git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

        if (remoteUrl == null) {
            dependency.addGitExceptions(new Exception(String.format("The repo has no remote url, Delete directory %s and try again", dependency.getDir())));
            return;
        } else if (!remoteUrl.equals(dependency.getUrl())) {
            dependency.addGitExceptions(new Exception(String.format("The repo has a different remote url, Delete directory %s and try again", dependency.getDir())));
            return;
        }

        final String targetCommit = dependency.getCommit();

        try {
            if (dependency.isKeepGitUpdated() && !isLocalCommit(targetCommit)) {
                final String localCommit = head().substring(0, 7);
                Logger.warn("Local version {} is not equal to target {} for {}", localCommit, targetCommit, dependency.getDir());

                if (hasLocalChanges()) {
                    dependency.addGitExceptions(new Exception(String.format("Git repo cannot be updated to %s, %s contains local changes. Commit or revert all changes manually.", targetCommit, dependency.getDir())));
                } else {
                    Logger.warn("Updating to version {} for {}", targetCommit, dependency.getDir());
                    update();
                }
            }

        } catch (IOException e) {
            gitExceptions(e);
        }
    }

    public boolean hasLocalChanges() {
        boolean changes = false;
        try {
            Status status = git.status().call();
            changes = !status.getAdded().isEmpty();
            changes |= !status.getChanged().isEmpty();
            changes |= !status.getRemoved().isEmpty();
            changes |= !status.getUntracked().isEmpty();
            changes |= !status.getModified().isEmpty();
            changes |= !status.getMissing().isEmpty();
        } catch (GitAPIException e) {
            gitExceptions(e);
        }
        return changes;
    }

    public void cloneRepo() {
        long start = System.currentTimeMillis();
        Logger.info("Clone started {} at version {}", dependency.getUrl(), dependency.getCommit());

        try {
            git = Git.cloneRepository()
                    .setGitDir(srki2k.localgitdependency.Constants.concatFile.apply(dependency.getDir(), Constants.DOT_GIT))
                    .setDirectory(dependency.getDir())
                    .setURI(dependency.getUrl())
                    .setRemote(Constants.DEFAULT_REMOTE_NAME)
                    .call();

            git.checkout().setName(dependency.getCommit()).call();
        } catch (GitAPIException e) {
            gitExceptions(e);
        }

        long spent = System.currentTimeMillis() - start;
        Logger.info("Clone finished {} ms", spent);
    }

    @Override
    public void update() {
        if (gitExceptions) return;

        final long start = System.currentTimeMillis();
        Logger.info("Update started {} at version {}", dependency.getUrl(), dependency.getCommit());

        try {
            git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
            git.checkout().setName(dependency.getCommit()).call();
        } catch (GitAPIException e) {
            gitExceptions(e);
        }

        final long spent = System.currentTimeMillis() - start;
        Logger.info("Update finished {} ms", spent);
    }

    @Override
    public void clearLocalChanges() {
        if (gitExceptions) return;

        if (hasLocalChanges()) {
            try {
                git.reset().setMode(ResetCommand.ResetType.HARD).call();
            } catch (GitAPIException e) {
                gitExceptions(e);
            }
        }
    }

    @Override
    public void close() {
        dependency = null;
        if (git != null) {
            git.close();
        }
    }

    private boolean isLocalCommit(String targetId) throws IOException {
        // Checking if local commit is equal to (starts with) requested one.
        String headId = head();
        if (headId.startsWith(targetId)) return true;

        // If not then we should check if there is a tag with given name pointing to current head.
        Ref tag = null;

        // Annotated tags need extra effort
        Ref peeledTag = null;

        try {
            tag = git.getRepository().getRefDatabase().exactRef(Constants.R_TAGS + targetId);
            peeledTag = tag == null ? null : git.getRepository().getRefDatabase().peel(tag);
        } catch (IOException e) {
            gitExceptions(e);
        }

        ObjectId tagObjectId = peeledTag != null ? peeledTag.getPeeledObjectId() : tag != null ? tag.getObjectId() : null;

        final String tagId = ObjectId.toString(tagObjectId);
        return Objects.equals(tagId, headId);
    }

    private String head() throws IOException {
        return ObjectId.toString(git.getRepository().resolve(Constants.HEAD));
    }

    private void gitExceptions(Exception exception) {
        gitExceptions = true;
        dependency.addGitExceptions(exception);
    }

    public boolean hasGitExceptions() {
        return gitExceptions;
    }
}
