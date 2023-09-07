package io.github.srdjanv.localgitdependency.git;

import static io.github.srdjanv.localgitdependency.git.GitInfo.TargetType.BRANCH;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;

import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.IOException;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.TagOpt;

public final class GitUtils {

    public static Git cloneRepo(GitInfo gitInfo) throws GitAPIException {
        final long start = System.currentTimeMillis();
        Git git = Git.cloneRepository()
                .setGitDir(FileUtil.concat(gitInfo.getDir(), Constants.DOT_GIT))
                .setDirectory(gitInfo.getDir())
                .setURI(gitInfo.getUrl())
                .setRemote(Constants.DEFAULT_REMOTE_NAME)
                .setCloneAllBranches(false)
                .setBare(false)
                .setNoCheckout(true)
                .call();

        gitInfo.setRefreshed();
        ManagerLogger.info("Finished cloning {} in {} ms", gitInfo.getUrl(), System.currentTimeMillis() - start);
        return git;
    }

    public static void checkout(GitRepo repo, GitInfo gitInfo) throws GitAPIException {
        repo.getGit()
                .checkout()
                .setCreateBranch(true)
                .setName(repo.getRemoteBranchName())
                .setStartPoint(gitInfo.getTargetRemote())
                .call();
    }

    public static void update(GitRepo repo, GitInfo gitInfo) throws GitAPIException, IOException {
        final long start = System.currentTimeMillis();
        final String localTargetBranch =
                switch (gitInfo.getTargetType()) {
                    case BRANCH -> gitInfo.getTargetLocal();
                    case TAG, COMMIT -> R_HEADS + repo.getRemoteBranchName();
                };
        if (repo.getGit().getRepository().getRefDatabase().firstExactRef(localTargetBranch) != null) {
            repo.getGit().checkout().setName(gitInfo.getTarget()).call();
        } else {
            repo.getGit()
                    .checkout()
                    .setCreateBranch(true)
                    .setName(repo.getRemoteBranchName())
                    .setStartPoint(gitInfo.getTargetRemote())
                    .call();
        }

        gitInfo.setRefreshed();
        ManagerLogger.info(
                "Finished updating {} to {} in {} ms",
                gitInfo.getUrl(),
                gitInfo.getTarget(),
                System.currentTimeMillis() - start);
    }

    public static void updateConfigAndFetch(GitRepo repo) throws IOException, GitAPIException {
        final StoredConfig config = repo.getGit().getRepository().getConfig();
        config.setString(
                ConfigConstants.CONFIG_BRANCH_SECTION,
                repo.getRemoteBranchName(),
                ConfigConstants.CONFIG_KEY_REMOTE,
                Constants.DEFAULT_REMOTE_NAME);
        config.setString(
                ConfigConstants.CONFIG_BRANCH_SECTION,
                repo.getRemoteBranchName(),
                ConfigConstants.CONFIG_KEY_MERGE,
                Constants.R_HEADS + repo.getRemoteBranchName());

        config.save();
        repo.getGit().fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    public static String getCorrespondingRemoteBranchNameFromCurrentTarget(GitRepo repo, GitInfo gitInfo)
            throws GitAPIException, IOException {
        final String remoteBranch;
        if (gitInfo.getTargetType() == BRANCH) {
            remoteBranch = gitInfo.getTarget();
        } else {
            final ObjectId objectid;
            if (gitInfo.getTargetType() == GitInfo.TargetType.TAG) {
                objectid = repo.getGit()
                        .getRepository()
                        .exactRef(gitInfo.getTargetRemote())
                        .getObjectId();
            } else { // commit
                objectid = ObjectId.fromString(gitInfo.getTargetRemote());
            }

            Map<ObjectId, String> map =
                    repo.getGit().nameRev().addPrefix(R_REMOTES).add(objectid).call();

            Optional<String> optional = map.values().stream().findFirst();
            String branch = optional.orElseThrow(() ->
                    new NoSuchElementException(String.format("Target: %s not found in remote", gitInfo.getTarget())));
            if (branch.contains("~")) {
                branch = branch.substring(0, branch.lastIndexOf("~"));
            }
            remoteBranch = branch.substring(branch.lastIndexOf("/") + 1);
        }
        return remoteBranch;
    }

    public static String getRepoHead(GitRepo repo) throws IOException {
        return ObjectId.toString(repo.getGit().getRepository().resolve(Constants.HEAD));
    }

    public static String getTargetCommit(GitRepo repo, String target) throws IOException {
        return ObjectId.toString(repo.getGit().getRepository().resolve(target));
    }

    public static boolean hasBranchLocalCommits(GitRepo repo, GitInfo gitInfo) throws IOException {
        if (gitInfo.getTargetType() == BRANCH) {
            Ref remoteBranch = repo.getGit().getRepository().getRefDatabase().exactRef(gitInfo.getTargetRemote());
            Ref localBranch = repo.getGit().getRepository().getRefDatabase().exactRef(gitInfo.getTargetLocal());
            if (localBranch == null) return false;
            return !Objects.equals(
                    remoteBranch.getObjectId().getName(),
                    localBranch.getObjectId().getName());
        }
        return false;
    }

    public static List<String> getLocalChanges(GitRepo repo) throws GitAPIException {
        Status status = repo.getGit().status().call();
        List<String> changes = new ArrayList<>();
        changes.addAll(status.getAdded());
        changes.addAll(status.getChanged());
        changes.addAll(status.getRemoved());
        changes.addAll(status.getUntracked());
        changes.addAll(status.getModified());
        changes.addAll(status.getMissing());
        return changes;
    }

    public static boolean isUpToDateWithRemote(GitRepo repo, GitInfo gitInfo) throws IOException {
        final String headId = repo.getHeadID();
        final String target = gitInfo.getTargetRemote();
        switch (gitInfo.getTargetType()) {
            case COMMIT -> {
                // Checking if local commit is equal to (starts with) requested one.
                return headId.startsWith(target);
            }
            case TAG -> {
                Ref tag = repo.getGit().getRepository().getRefDatabase().exactRef(target);

                // Annotated tags need extra effort
                Ref peeledTag = null;
                if (tag != null) {
                    peeledTag = repo.getGit().getRepository().getRefDatabase().peel(tag);
                }

                ObjectId tagObjectId = null;
                if (peeledTag != null) {
                    tagObjectId = peeledTag.getPeeledObjectId();
                }
                if (tag != null && tagObjectId == null) {
                    tagObjectId = tag.getObjectId();
                }

                if (tagObjectId == null) {
                    throw new RuntimeException(String.format("Unable to locate remote tag: '%s", gitInfo.getTarget()));
                }

                return Objects.equals(ObjectId.toString(tagObjectId), headId);
            }
            case BRANCH -> {
                // Search for the target remote
                Ref remote = repo.getGit().getRepository().getRefDatabase().exactRef(target);
                if (remote != null) {
                    return Objects.equals(remote.getObjectId().getName(), headId);
                }
                throw new RuntimeException(String.format("Unable to locate remote branch: '%s", gitInfo.getTarget()));
            }
            default -> throw new IllegalStateException();
        }
    }

    private GitUtils() {}
}
