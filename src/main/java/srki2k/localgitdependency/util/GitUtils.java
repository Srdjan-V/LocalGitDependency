package srki2k.localgitdependency.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import org.gradle.api.GradleException;
import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Some code has been taken from https://github.com/alexvasilkov/GradleGitDependenciesPlugin
public class GitUtils {
    private GitUtils() {
    }

    public static void setupGit(Dependency dep) {
        Git git;

        try {
            git = openGit(dep);
        } catch (IOException ignored) {
            try {
                clone(dep);
            } catch (GitAPIException e) {
                dep.addGitExceptions(e);
            }
            return;
        }

        try {
            final String remoteUrl = git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

            if (remoteUrl == null) {
                dep.addGitExceptions(new Exception(String.format("The repo has no remote url, Delete directory %s and try again", dep.getDir())));
                return;
            } else if (!remoteUrl.equals(dep.getUrl())) {
                dep.addGitExceptions(new Exception(String.format("The repo has a different remote url, Delete directory %s and try again", dep.getDir())));
                return;
            }

            final String targetCommit = dep.getCommit();

            try {
                if (dep.isKeepGitUpdated() && !isLocalCommit(git, targetCommit)) {
                    final String localCommit = head(git).substring(0, 7);
                    Logger.warn("Local version {} is not equal to target {} for {}", localCommit, targetCommit, dep.getDir());

                    if (hasLocalChanges(git)) {
                        dep.addGitExceptions(new Exception(String.format("Git repo cannot be updated to %s, %s contains local changes. Commit or revert all changes manually.", targetCommit, dep.getDir())));
                    } else {
                        Logger.warn("Updating to version {} for {}", targetCommit, dep.getDir());
                        update(git, dep);
                    }
                }

            } catch (GitAPIException | IOException e) {
                dep.addGitExceptions(e);
            }

        } finally {
            git.close();
        }
    }

    public static Git openGit(Dependency dep) throws IOException {
        return Git.open(dep.getDir());
    }

    public static String head(Git git) throws IOException {
        return ObjectId.toString(git.getRepository().resolve(Constants.HEAD));
    }

    public static boolean isLocalCommit(Git git, String targetId) throws IOException {
        // Checking if local commit is equal to (starts with) requested one.
        final String headId;
        headId = head(git);
        if (headId.startsWith(targetId)) return true;

        // If not then we should check if there is a tag with given name pointing to current head.
        final Ref tag;

        // Annotated tags need extra effort
        Ref peeledTag;

        try {
            tag = git.getRepository().getRefDatabase().exactRef(Constants.R_TAGS + targetId);
            peeledTag = tag == null ? null : git.getRepository().getRefDatabase().peel(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectId tagObjectId = peeledTag != null ? peeledTag.getPeeledObjectId() : tag != null ? tag.getObjectId() : null;

        final String tagId = ObjectId.toString(tagObjectId);
        return Objects.equals(tagId, headId);
    }

    public static boolean hasLocalChanges(Git git) throws GitAPIException {
        final Status status = git.status().call();
        final List<String> changes = new ArrayList<>();
        changes.addAll(status.getAdded());
        changes.addAll(status.getChanged());
        changes.addAll(status.getRemoved());
        changes.addAll(status.getUntracked());
        changes.addAll(status.getModified());
        changes.addAll(status.getMissing());
        return !changes.isEmpty();
    }

    public static boolean hasLocalChangesInDir(File dir) throws IOException, GitAPIException {
        try (Git git = Git.open(dir)) {
            return hasLocalChanges(git);
        }
    }

    public static void update(Git git, Dependency dep) throws GitAPIException {
        final long start = System.currentTimeMillis();
        Logger.info("Update started {} at version {}", dep.getUrl(), dep.getCommit());

        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
        git.checkout().setName(dep.getCommit()).call();

        final long spent = System.currentTimeMillis() - start;
        Logger.info("Update finished {} ms", spent);
    }

    public static void clone(Dependency dep) throws GitAPIException {
        final long start = System.currentTimeMillis();
        Logger.info("Clone started {} at version {}", dep.getUrl(), dep.getCommit());

        try (final Git git = Git.cloneRepository()
                .setDirectory(dep.getDir())
                .setURI(dep.getUrl())
                .setRemote("origin")
                .setNoCheckout(true)
                .call()) {
            git.checkout().setName(dep.getCommit()).call();
        }

        final long spent = System.currentTimeMillis() - start;
        Logger.info("Clone finished {} ms", spent);
    }

    public static void clearLocalChanges(Dependency dep) throws GitAPIException, IOException {
        try (final Git git = openGit(dep)) {
            if (hasLocalChanges(git)) {
                git.reset().setMode(ResetCommand.ResetType.HARD).call();
            }
        }
    }

}
