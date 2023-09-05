package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.util.Collections;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

public class GitRepo implements AutoCloseable {
    private final Git git;
    private final boolean cloned;
    private final String remoteBranchName;
    private final GitInfo gitInfo;
    private final String headID;
    private final List<String> localChanges;

    public GitRepo(GitInfo gitInfo) throws Exception {
        this.gitInfo = gitInfo;
        {
            Git git;
            boolean cloned;
            try {
                git = Git.open(FileUtil.concat(gitInfo.getDir(), Constants.DOT_GIT));
                cloned = false;

            } catch (RepositoryNotFoundException initRepo) {
                git = GitUtils.cloneRepo(gitInfo);
                cloned = true;
            }

            this.git = git;
            this.cloned = cloned;
        }

        this.remoteBranchName = GitUtils.getCorrespondingRemoteBranchNameFromCurrentTarget(this, gitInfo);
        if (cloned) {
            GitUtils.updateConfigAndFetch(this);
            GitUtils.checkout(this, gitInfo);
        } else {
            final String remoteUrl =
                    git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

            if (remoteUrl == null) {
                throw new Exception(String.format(
                        "The repo has no remote url, Delete directory %s and try again", gitInfo.getDir()));
            } else if (!remoteUrl.equals(gitInfo.getUrl()))
                throw new Exception(String.format(
                        "The repo has a different remote url, Delete directory %s and try again", gitInfo.getDir()));

            if (!gitInfo.getTarget()
                    .equals(gitInfo.getDependency().getPersistentInfo().getGitTarget())) {
                GitUtils.updateConfigAndFetch(this);
                gitInfo.getDependency().getPersistentInfo().setGitTarget(gitInfo.getTarget());
            }
        }

        this.headID = GitUtils.getRepoHead(this);
        this.localChanges = GitUtils.getLocalChanges(this);
    }

    public List<String> getLocalChanges() {
        return localChanges == null ? Collections.emptyList() : localChanges;
    }

    public Git getGit() {
        return git;
    }

    public boolean isCloned() {
        return cloned;
    }

    public GitInfo getGitInfo() {
        return gitInfo;
    }

    public String getRemoteBranchName() {
        return remoteBranchName;
    }

    public String getHeadID() {
        return headID;
    }

    @Override
    public void close() {
        if (git != null) git.close();
    }
}
