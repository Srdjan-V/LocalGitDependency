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
    public void initRepos() {
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            initRepo(dependency);
        }
    }

    @Override
    public void initRepo(Dependency dependency) {
        try (var repo = new GitRepo(dependency.getGitInfo())) {
            updateRepoIfNeeded(repo);
            updateTaskTriggersSHA(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRepoIfNeeded(GitRepo repo) throws GitAPIException, IOException {
        if (repo.isCloned()) return;
        if (!repo.getGitInfo().isKeepGitUpdated()) return;

        if (repo.getLocalChanges().isEmpty() && GitUtils.isUpToDateWithRemote(repo, repo.getGitInfo())) return;
        final String targetCommit = GitUtils.getTargetCommit(
                        repo, repo.getGitInfo().getTargetLocal())
                .substring(0, 7); // TODO: 04/09/2023 test
        if (!repo.getLocalChanges().isEmpty() || GitUtils.hasBranchLocalCommits(repo, repo.getGitInfo())) {
            throw new RuntimeException(String.format(
                    "Git repo cannot be updated to %s, %s contains local changes. Commit and push or revert all changes manually.",
                    targetCommit, repo.getGitInfo().getDir()));
        } else {
            ManagerLogger.info(
                    "Updating to version {} for {}",
                    targetCommit,
                    repo.getGitInfo().getDependency().getName());
            GitUtils.update(repo, repo.getGitInfo());
        }
    }

    private void updateTaskTriggersSHA(GitRepo repo) throws IOException {
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

        if (tags.isEmpty()) return;
        ManagerLogger.info(
                "Dependency {} has new local changes, marking [{}] {} to be run",
                repo.getGitInfo().getDependency().getName(),
                tags,
                tags.size() > 1 ? "stages" : "stage");
    }
}
