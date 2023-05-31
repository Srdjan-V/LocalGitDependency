package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.util.sha1.SHA1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.github.srdjanv.localgitdependency.git.GitInfo.TargetType.BRANCH;
import static org.eclipse.jgit.lib.Constants.*;

// Some code has been taken from
// https://github.com/alexvasilkov/GradleGitDependenciesPlugin/blob/master/src/main/groovy/com/alexvasilkov/gradle/git/utils/GitUtils.groovy
final class GitWrapper implements AutoCloseable {
    private List<Exception> gitExceptions;
    private String shaLocalChanges;
    Git git;
    GitInfo gitInfo;

    GitWrapper(GitInfo gitInfo) {
        this.gitInfo = gitInfo;
        try {
            git = Git.open(io.github.srdjanv.localgitdependency.Constants.concatFile.apply(gitInfo.getDir(), Constants.DOT_GIT));
        } catch (RepositoryNotFoundException initRepo) {
            try {
                cloneRepo();
            } catch (GitAPIException | IOException e) {
                addGitExceptions(e);
            }
        } catch (Exception exception) {
            addGitExceptions(exception);
        }
    }

    public void setup() {
        try {
            if (hasGitExceptions()) return;
            final String remoteUrl = git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

            if (remoteUrl == null) {
                addGitExceptions(new Exception(String.format("The repo has no remote url, Delete directory %s and try again", gitInfo.getDir())));
                return;
            } else if (!remoteUrl.equals(gitInfo.getUrl())) {
                addGitExceptions(new Exception(String.format("The repo has a different remote url, Delete directory %s and try again", gitInfo.getDir())));
                return;
            }

            final String targetCommit = gitInfo.getTarget();
            if (gitInfo.isKeepGitUpdated()) {
                saveConfigAndFetch();
                if (!isUpToDateWithRemote()) {
                    final String localCommit = head().substring(0, 7);
                    ManagerLogger.info("Local version {} is not equal to remote target {} for {}", localCommit, targetCommit, gitInfo.getDependency().getName());

                    if (hasLocalChanges()) {
                        addGitExceptions(new Exception(String.format("Git repo cannot be updated to %s, %s contains local changes. Commit and push or revert all changes manually.", targetCommit, gitInfo.getDir())));
                    } else if (hasBranchLocalCommits()) {
                        addGitExceptions(new Exception(String.format("Git repo cannot be updated to %s, %s contains local commits. Push to the remote or revert all changes manually.", targetCommit, gitInfo.getDir())));
                    } else {
                        ManagerLogger.info("Updating to version {} for {}", targetCommit, gitInfo.getDependency().getName());
                        update();
                    }
                }
            }
        } catch (Exception e) {
            addGitExceptions(e);
        } finally {
            try {
                checkSHA1();
            } catch (IOException | GitAPIException e) {
                addGitExceptions(e);
            }
        }
    }

    private void checkSHA1() throws IOException, GitAPIException {
        String persistentWorkingDirSHA1 = gitInfo.getDependency().getPersistentInfo().getWorkingDirSHA1();
        String workingDirSHA1;
        if (hasLocalChanges()) {
            workingDirSHA1 = shaLocalChanges;
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
        ManagerLogger.info("Dependency {} has new local changes, marking dependency to be rebuild", gitInfo.getDependency().getName());
    }

    boolean hasLocalChanges() throws GitAPIException, IOException {
        if (shaLocalChanges != null) return true;

        try {
            Status status = git.status().call();
            List<String> changes = new ArrayList<>();

            changes.addAll(status.getAdded());
            changes.addAll(status.getChanged());
            changes.addAll(status.getRemoved());
            changes.addAll(status.getUntracked());
            changes.addAll(status.getModified());
            changes.addAll(status.getMissing());

            if (!changes.isEmpty()) {
                SHA1 sha1 = SHA1.newInstance();
                byte[] buffer = new byte[4096];
                int read;
                for (String file : changes) {
                    var filePath = new File(gitInfo.getDir(), file);

                    if (filePath.exists()) {
                        try (FileInputStream inputStream = new FileInputStream(filePath)) {
                            while ((read = inputStream.read(buffer)) > 0) {
                                sha1.update(buffer, 0, read);
                            }
                        }
                    } else {
                        sha1.update(file.getBytes(StandardCharsets.UTF_8));
                    }
                }
                shaLocalChanges = sha1.toObjectId().getName();
                return true;
            } else {
                return false;
            }
        } catch (GitAPIException | IOException e) {
            addGitExceptions(e);
            throw e;
        }
    }

    private void cloneRepo() throws GitAPIException, IOException {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Clone started {} at version {}", gitInfo.getUrl(), gitInfo.getTarget());

        git = Git.cloneRepository().
                setGitDir(io.github.srdjanv.localgitdependency.Constants.concatFile.apply(gitInfo.getDir(), Constants.DOT_GIT)).
                setDirectory(gitInfo.getDir()).
                setURI(gitInfo.getUrl()).
                setRemote(Constants.DEFAULT_REMOTE_NAME).
                setCloneAllBranches(false).
                setBare(false).
                setNoCheckout(true).
                call();

        saveConfigAndFetch();
        git.checkout().
                setCreateBranch(true).
                setName(getCorrespondingRemoteBranch()).
                setStartPoint(gitInfo.getTargetRemote()).
                call();

        gitInfo.setRefreshed();
        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Clone finished in {} ms", spent);
    }

    public void update() throws GitAPIException, IOException {
        final long start = System.currentTimeMillis();
        ManagerLogger.info("Update started {} at version {}", gitInfo.getUrl(), gitInfo.getTarget());

        String localTargetBranch = null;
        switch (gitInfo.getTargetType()) {
            case BRANCH -> {
                localTargetBranch = gitInfo.getTargetLocal();
            }
            case TAG, COMMIT -> {
                localTargetBranch = R_HEADS + getCorrespondingRemoteBranch();
            }
        }
        if (git.getRepository().getRefDatabase().firstExactRef(localTargetBranch) != null) {
            git.checkout().
                    setName(gitInfo.getTarget()).
                    call();
        } else {
            git.checkout().
                    setCreateBranch(true).
                    setName(getCorrespondingRemoteBranch()).
                    setStartPoint(gitInfo.getTargetRemote()).
                    call();
        }

        gitInfo.setRefreshed();
        final long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Update finished in {} ms", spent);
    }

    boolean isUpToDateWithRemote() throws GitAPIException, IOException {
        final String headId = head();
        final String target = gitInfo.getTargetRemote();
        switch (gitInfo.getTargetType()) {
            case COMMIT -> {
                // Checking if local commit is equal to (starts with) requested one.
                return headId.startsWith(target);
            }
            case TAG -> {
                Ref tag = git.getRepository().getRefDatabase().exactRef(target);

                // Annotated tags need extra effort
                Ref peeledTag = null;
                if (tag != null) {
                    peeledTag = git.getRepository().getRefDatabase().peel(tag);
                }

                ObjectId tagObjectId = null;
                if (peeledTag != null) {
                    tagObjectId = peeledTag.getPeeledObjectId();
                }
                if (tag != null && tagObjectId == null) {
                    tagObjectId = tag.getObjectId();
                }

                if (tagObjectId == null) {
                    throw new GitAPIException(String.format("Was not able to locate tag with id %s", target)) {
                    };
                }

                return Objects.equals(ObjectId.toString(tagObjectId), headId);
            }
            case BRANCH -> {
                // Search for a remote
                Ref remote = git.getRepository().getRefDatabase().exactRef(target);
                if (remote != null) {
                    return Objects.equals(remote.getObjectId().getName(), headId);
                }
                throw new GitAPIException("No remote nor any matching tags where found") {
                };
            }
            default -> throw new IllegalStateException();
        }
    }

    private boolean hasBranchLocalCommits() throws Exception {
        if (gitInfo.getTargetType() == BRANCH) {
            Ref remoteBranch = git.getRepository().getRefDatabase().exactRef(gitInfo.getTargetRemote());
            Ref localBranch = git.getRepository().getRefDatabase().exactRef(gitInfo.getTargetLocal());
            if (localBranch == null) {
                return false;
            }
            return !Objects.equals(remoteBranch.getObjectId().getName(), localBranch.getObjectId().getName());
        } else {
            return false;
        }
    }

    String head() throws IOException {
        return ObjectId.toString(git.getRepository().resolve(Constants.HEAD));
    }

    //Gets the branch from the remote based on the target type

    private String remoteBranch;

    private String getCorrespondingRemoteBranch() throws GitAPIException, IOException {
        if (remoteBranch == null) {
            if (gitInfo.getTargetType() == BRANCH) {
                remoteBranch = gitInfo.getTarget();
            } else {
                final ObjectId objectid;
                if (gitInfo.getTargetType() == GitInfo.TargetType.TAG) {
                    objectid = git.getRepository().exactRef(gitInfo.getTargetRemote()).getObjectId();
                } else {
                    objectid = ObjectId.fromString(gitInfo.getTargetRemote());
                }

                Map<ObjectId, String> map = git
                        .nameRev()
                        .addPrefix(R_REMOTES)
                        .add(objectid)
                        .call();

                Optional<String> optional = map.values().stream().findFirst();
                String branch = optional.orElseThrow(() -> new RuntimeException(String.format("Target: %s not found in remote", gitInfo.getTarget())));
                if (branch.contains("~")) {
                    branch = branch.substring(0, branch.lastIndexOf("~"));
                }
                remoteBranch = branch.substring(branch.lastIndexOf("/") + 1);
            }
        }
        return remoteBranch;
    }

    private void saveConfigAndFetch() throws IOException, GitAPIException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString(
                ConfigConstants.CONFIG_BRANCH_SECTION, getCorrespondingRemoteBranch(),
                ConfigConstants.CONFIG_KEY_REMOTE, Constants.DEFAULT_REMOTE_NAME);
        config.setString(
                ConfigConstants.CONFIG_BRANCH_SECTION, getCorrespondingRemoteBranch(),
                ConfigConstants.CONFIG_KEY_MERGE, Constants.R_HEADS + getCorrespondingRemoteBranch());

        config.save();
        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    void addGitExceptions(Exception exception) {
        if (gitExceptions == null) {
            gitExceptions = new ArrayList<>();
        }
        gitExceptions.add(exception);
    }

    boolean hasGitExceptions() {
        return gitExceptions != null;
    }

    public GitReport getGitReport() {
        return new GitReport(gitExceptions);
    }

    @Override
    public void close() {
        gitInfo = null;
        if (git != null) {
            git.close();
        }
    }
}
