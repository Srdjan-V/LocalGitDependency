package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

public class GitRepo implements AutoCloseable {
    private final Git git;
    private final boolean cloned;
    private final String remoteBranchName;
    private final GitInfo gitInfo;
    private final String headID;
    private final List<String> localChanges;

    public GitRepo(GitInfo gitInfo) throws GitAPIException, IOException, GitRepoInitializationException {
        this.gitInfo = gitInfo;
        {
            boolean cloned = false;
            Git git = null;
            try {
                git = Git.open(FileUtil.concat(gitInfo.getDir(), Constants.DOT_GIT));
            } catch (RepositoryNotFoundException initRepo) {
                cloned = true;
                git = GitUtils.cloneRepo(gitInfo);
            } finally {
                this.git = git;
                this.cloned = cloned;
            }
        }

        try {
            this.remoteBranchName = GitUtils.getCorrespondingRemoteBranchNameFromCurrentTarget(this);
            if (cloned) {
                GitUtils.updateConfigAndFetch(this);
                GitUtils.checkout(this);
            } else {
                final String remoteUrl =
                        git.getRepository().getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");

                if (remoteUrl == null) {
                    throw new Exception(String.format(
                            "The repo has no remote url, delete directory: %s and try again", gitInfo.getDir()));
                } else if (!remoteUrl.equals(gitInfo.getUrl()))
                    throw new Exception(String.format(
                            "The repo has a different remote url, delete directory: %s and try again",
                            gitInfo.getDir()));

                if (!gitInfo.getTarget()
                        .equals(gitInfo.getDependency().getPersistentInfo().getGitTarget())) {
                    GitUtils.updateConfigAndFetch(this);
                    gitInfo.getDependency().getPersistentInfo().setGitTarget(gitInfo.getTarget());
                }
            }

            this.headID = GitUtils.getRepoHead(this);
            this.localChanges = GitUtils.getLocalChanges(this);
        } catch (Exception e) {
            close();
            throw new GitRepoInitializationException(e);
        }
    }

    public List<String> getLocalChanges() {
        return localChanges;
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

    public static class GitRepoInitializationException extends Exception {
        public GitRepoInitializationException(Exception e) {
            super(e);
        }
    }
}
