package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.HashingUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.errors.GitAPIException;

final class GitManager extends ManagerBase implements IGitManager {
    GitManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {}

    @Override
    public boolean initRepos() {
        boolean didWork = false;
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            didWork |= initRepo(dependency);
        }
        return didWork;
    }

    @Override
    public boolean initRepo(Dependency dependency) {
        boolean didWork = false;
        try (var repo = new GitRepo(dependency.getGitInfo())) {
            didWork |= updateRepoIfNeeded(repo);
            didWork |= updateTaskTriggersSHA(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return didWork;
    }

    public boolean updateRepoIfNeeded(GitRepo repo) throws GitAPIException, IOException {
        if (repo.isCloned()) return false;
        if (!repo.getGitInfo().isKeepGitUpdated()) return false;

        if (repo.getLocalChanges().isEmpty() && GitUtils.isUpToDateWithRemote(repo)) return false;
        final String targetCommit = GitUtils.getTargetCommit(
                        repo, repo.getGitInfo().getTargetLocal())
                .substring(0, 7); // TODO: 04/09/2023 test
        if (!repo.getLocalChanges().isEmpty() || GitUtils.hasBranchLocalCommits(repo)) {
            if (repo.getGitInfo().isForceGitUpdate()) {
                GitUtils.update(repo);
                return true;
            } else
                throw new RuntimeException(String.format(
                        "Git repo cannot be updated to %s, %s contains local changes."
                                + " Commit and push or revert all changes manually. Or enable ForceGitUpdate to discard the local changes",
                        targetCommit, repo.getGitInfo().getDir()));
        } else {
            ManagerLogger.info(
                    "Updating to version {} for {}",
                    targetCommit,
                    repo.getGitInfo().getDependency().getName());
            GitUtils.update(repo);
            return true;
        }
    }

    private boolean updateTaskTriggersSHA(GitRepo repo) throws IOException {
        final String startupTasksTriggersSHA1;
        final String probeTasksTriggersSHA1;
        final String buildTasksTriggersSHA1;

        if (!repo.getLocalChanges().isEmpty()) {
            var launchers = repo.getGitInfo().getDependency().getGradleInfo().getLaunchers();
            String hash;
            hash = HashingUtil.generateSHA1(
                    repo.getGitInfo().getDir(),
                    repo.getLocalChanges().stream()
                            .filter(string -> launchers
                                    .getStartup()
                                    .getTaskTriggers()
                                    .get()
                                    .contains(string))
                            .collect(Collectors.toList()));
            startupTasksTriggersSHA1 = hash == null ? repo.getHeadID() : hash;

            hash = HashingUtil.generateSHA1(
                    repo.getGitInfo().getDir(),
                    repo.getLocalChanges().stream()
                            .filter(string ->
                                    launchers.getProbe().getTaskTriggers().get().contains(string))
                            .collect(Collectors.toList()));
            probeTasksTriggersSHA1 = hash == null ? repo.getHeadID() : hash;

            if (launchers.getBuild().getTaskTriggers().get().isEmpty()) {
                hash = HashingUtil.generateSHA1(repo.getGitInfo().getDir(), repo.getLocalChanges());
            } else {
                hash = HashingUtil.generateSHA1(
                        repo.getGitInfo().getDir(),
                        repo.getLocalChanges().stream()
                                .filter(string -> launchers
                                        .getBuild()
                                        .getTaskTriggers()
                                        .get()
                                        .contains(string))
                                .collect(Collectors.toList()));
            }
            buildTasksTriggersSHA1 = hash == null ? repo.getHeadID() : hash;
        } else {
            startupTasksTriggersSHA1 = repo.getHeadID();
            probeTasksTriggersSHA1 = repo.getHeadID();
            buildTasksTriggersSHA1 = repo.getHeadID();
        }

        final var persistentInfo = repo.getGitInfo().getDependency().getPersistentInfo();
        final var launchers = repo.getGitInfo().getDependency().getGradleInfo().getLaunchers();
        final var tags = new ArrayList<String>(3);

        final String persistentStartupTasksTriggersSHA1 = persistentInfo.getStartupTasksTriggersSHA1();
        if (persistentStartupTasksTriggersSHA1 == null) {
            launchers.getStartup().getIsRunNeeded().set(true);
            persistentInfo.setStartupTasksTriggersSHA1(startupTasksTriggersSHA1);
        } else if (!persistentStartupTasksTriggersSHA1.equals(startupTasksTriggersSHA1)) {
            tags.add("startup");
            launchers.getStartup().getIsRunNeeded().set(true);
            persistentInfo.setStartupTasksTriggersSHA1(startupTasksTriggersSHA1);
        }

        final String persistentProbeTasksTriggersSHA1 = persistentInfo.getProbeTasksTriggersSHA1();
        if (persistentProbeTasksTriggersSHA1 == null) {
            launchers.getProbe().getIsRunNeeded().set(true);
            persistentInfo.setProbeTasksTriggersSHA1(probeTasksTriggersSHA1);
        } else if (!persistentProbeTasksTriggersSHA1.equals(probeTasksTriggersSHA1)) {
            tags.add("probe");
            launchers.getProbe().getIsRunNeeded().set(true);
            persistentInfo.setProbeTasksTriggersSHA1(probeTasksTriggersSHA1);
        }

        final String persistentBuildTasksTriggersSHA1 = persistentInfo.getBuildTasksTriggersSHA1();
        if (persistentBuildTasksTriggersSHA1 == null) {
            launchers.getBuild().getIsRunNeeded().set(true);
            persistentInfo.setBuildTasksTriggersSHA1(buildTasksTriggersSHA1);
        } else if (!persistentBuildTasksTriggersSHA1.equals(buildTasksTriggersSHA1)) {
            tags.add("build");
            launchers.getBuild().getIsRunNeeded().set(true);
            persistentInfo.setBuildTasksTriggersSHA1(buildTasksTriggersSHA1);
        }

        if (!tags.isEmpty()) {
            ManagerLogger.info(
                    "Dependency {} has new local changes, marking {} {} to be run",
                    repo.getGitInfo().getDependency().getName(),
                    tags,
                    tags.size() > 1 ? "stages" : "stage");
            return true;
        } else return false;
    }
}
