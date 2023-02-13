package srki2k.localgitdependency.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.util.sha1.SHA1;
import srki2k.localgitdependency.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO: 10/02/2023 redo some parts of this class
// Some code has been taken from
// https://github.com/alexvasilkov/GradleGitDependenciesPlugin/blob/master/src/main/groovy/com/alexvasilkov/gradle/git/utils/GitUtils.groovy
class GitObjectWrapper implements AutoCloseable, GitTasks {
    private boolean cloned;
    private boolean gitExceptions;
    private Git git;
    private GitInfo gitInfo;
    private List<File> changedFiles;

    GitObjectWrapper(GitInfo gitInfo) {
        this.gitInfo = gitInfo;
        try {
            git = Git.open(srki2k.localgitdependency.Constants.concatFile.apply(gitInfo.getDir(), Constants.DOT_GIT));
        } catch (RepositoryNotFoundException initRepo) {
            cloned = true;
            try {
                cloneRepo();
            } catch (GitAPIException exception) {
                gitExceptions(exception);
            }
        } catch (Exception exception) {
            gitExceptions(exception);
        }
    }

    @Override
    public void setup() {
        try {
            if (gitExceptions) return;
            if (cloned) {
                checkSHA1();
                return;
            }
            final String remoteUrl = git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

            if (remoteUrl == null) {
                gitInfo.addGitExceptions(new Exception(String.format("The repo has no remote url, Delete directory %s and try again", gitInfo.getDir())));
                return;
            } else if (!remoteUrl.equals(gitInfo.getUrl())) {
                gitInfo.addGitExceptions(new Exception(String.format("The repo has a different remote url, Delete directory %s and try again", gitInfo.getDir())));
                return;
            }

            final String targetCommit = gitInfo.getCommit();


            if (gitInfo.isKeepGitUpdated() && !isLocalCommit(targetCommit)) {
                final String localCommit = head().substring(0, 7);
                Logger.warn("Local version {} is not equal to target {} for {}", localCommit, targetCommit, gitInfo.getDir());

                if (hasLocalChanges()) {
                    gitInfo.addGitExceptions(new Exception(String.format("Git repo cannot be updated to %s, %s contains local changes. Commit or revert all changes manually.", targetCommit, gitInfo.getDir())));
                } else {
                    Logger.warn("Updating to version {} for {}", targetCommit, gitInfo.getDir());
                    update();
                }
            }

            checkSHA1();
        } catch (Exception e) {
            gitExceptions(e);
        }
    }

    private void checkSHA1() throws IOException, GitAPIException, NoSuchFieldException, IllegalAccessException {
        String persistentWorkingDirSHA1 = gitInfo.getDependency().getPersistentInfo().getWorkingDirSHA1();
        String workingDirSHA1;
        if (hasLocalChanges()) {
            workingDirSHA1 = createSHA1OfLocalChanges();
        } else {
            workingDirSHA1 = head();
        }

        if (persistentWorkingDirSHA1 == null) {
            gitInfo.setRefreshed();
            gitInfo.getDependency().getPersistentInfo().setWorkingDirSHA1(workingDirSHA1);
            return;
        }

        if (persistentWorkingDirSHA1.equals(workingDirSHA1)) return;
        gitInfo.setRefreshed();
        gitInfo.getDependency().getPersistentInfo().setWorkingDirSHA1(workingDirSHA1);
    }

    private boolean hasLocalChanges() throws NoSuchFieldException, IllegalAccessException, GitAPIException {
        if (changedFiles != null && changedFiles.isEmpty()) return false;
        changedFiles = new ArrayList<>();

        Status status = git.status().call();
        //Use reflection to skip the creation of unmodifiable sets
        Field diffField = Status.class.getDeclaredField("diff");
        diffField.setAccessible(true);
        IndexDiff diff = (IndexDiff) diffField.get(status);

        addChanges(diff.getAdded());
        addChanges(diff.getChanged());
        addChanges(diff.getRemoved());
        addChanges(diff.getUntracked());
        addChanges(diff.getModified());
        addChanges(diff.getMissing());

        return !changedFiles.isEmpty();
    }

    private void addChanges(Set<String> set) {
        for (String file : set) {
            changedFiles.add(new File(gitInfo.getDir(), file));
        }
    }

    private void cloneRepo() throws GitAPIException {
        long start = System.currentTimeMillis();
        Logger.info("Clone started {} at version {}", gitInfo.getUrl(), gitInfo.getCommit());

        git = Git.cloneRepository()
                .setGitDir(srki2k.localgitdependency.Constants.concatFile.apply(gitInfo.getDir(), Constants.DOT_GIT))
                .setDirectory(gitInfo.getDir())
                .setURI(gitInfo.getUrl())
                .setRemote(Constants.DEFAULT_REMOTE_NAME)
                .setNoCheckout(true)
                .call();

        git.checkout().setName(gitInfo.getCommit()).call();
        gitInfo.setRefreshed();

        long spent = System.currentTimeMillis() - start;
        Logger.info("Clone finished {} ms", spent);
    }

    private void update() throws GitAPIException {
        final long start = System.currentTimeMillis();
        Logger.info("Update started {} at version {}", gitInfo.getUrl(), gitInfo.getCommit());

        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
        git.checkout().setName(gitInfo.getCommit()).call();
        gitInfo.setRefreshed();

        final long spent = System.currentTimeMillis() - start;
        Logger.info("Update finished {} ms", spent);
    }

    @Override
    public void clearLocalChanges() {
        if (gitExceptions) return;

        try {
            if (hasLocalChanges()) {
                git.reset().setMode(ResetCommand.ResetType.HARD).call();
                gitInfo.setRefreshed();
            }
        } catch (Exception e) {
            gitExceptions(e);
        }
    }

    public String createSHA1OfLocalChanges() throws IOException {
        SHA1 sha1 = SHA1.newInstance();
        byte[] buffer = new byte[4096];
        int read;

        for (File file : changedFiles) {
            // TODO: 13/02/2023 maybe implement a way to skip checking for existing files
            if (file.exists()) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    while ((read = inputStream.read(buffer)) > 0) {
                        sha1.update(buffer, 0, read);
                    }
                }
            } else {
                sha1.update(file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
            }
        }
        return ObjectId.fromRaw(sha1.digest()).getName();
    }

    @Override
    public void close() {
        gitInfo = null;
        if (git != null) {
            git.close();
        }
    }

    private boolean isLocalCommit(String targetId) throws Exception {
        // Checking if local commit is equal to (starts with) requested one.
        String headId = head();
        if (headId.startsWith(targetId)) return true;

        // If not then we should check if there is a tag with given name pointing to current head.
        Ref tag = git.getRepository().getRefDatabase().exactRef(Constants.R_TAGS + targetId);

        // Annotated tags need extra effort
        Ref peeledTag = tag == null ? null : git.getRepository().getRefDatabase().peel(tag);

        ObjectId tagObjectId = peeledTag != null ? peeledTag.getPeeledObjectId() : tag != null ? tag.getObjectId() : null;

        // Search for a remote
        if (tagObjectId == null) {
            Ref remote = git.getRepository().getRefDatabase().exactRef(Constants.R_REMOTES + targetId);
            if (remote != null) {
                return Objects.equals(ObjectId.toString(remote.getObjectId()), headId);
            }
            throw new Exception("No remote nor any matching tags where found");
        }

        return Objects.equals(ObjectId.toString(tagObjectId), headId);
    }

    private String head() throws IOException {
        return ObjectId.toString(git.getRepository().resolve(Constants.HEAD));
    }

    private void gitExceptions(Exception exception) {
        gitExceptions = true;
        gitInfo.addGitExceptions(exception);
    }

    public boolean hasGitExceptions() {
        return gitExceptions;
    }
}
