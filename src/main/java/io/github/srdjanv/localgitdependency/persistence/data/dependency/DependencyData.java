package io.github.srdjanv.localgitdependency.persistence.data.dependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import java.util.Set;

@NonNullData
public class DependencyData {
    private String initFileSHA1;
    private String gitTarget;
    private Boolean startupTasksSuccessful;
    private String startupTasksTriggersSHA1;
    private Boolean probeTasksSuccessful;
    private String probeTasksTriggersSHA1;
    private Boolean buildTasksSuccessful;
    private String buildTasksTriggersSHA1;
    private Set<Dependency.Type> buildTypes;

    public String getInitFileSHA1() {
        return initFileSHA1;
    }

    public void setInitFileSHA1(String initFileSHA1) {
        this.initFileSHA1 = initFileSHA1;
    }

    public String getGitTarget() {
        return gitTarget;
    }

    public void setGitTarget(String gitTarget) {
        this.gitTarget = gitTarget;
    }

    public Boolean getStartupTasksSuccessful() {
        return startupTasksSuccessful;
    }

    public void setStartupTasksSuccessful(Boolean startupTasksSuccessful) {
        this.startupTasksSuccessful = startupTasksSuccessful;
    }

    public String getStartupTasksTriggersSHA1() {
        return startupTasksTriggersSHA1;
    }

    public void setStartupTasksTriggersSHA1(String startupTasksTriggersSHA1) {
        this.startupTasksTriggersSHA1 = startupTasksTriggersSHA1;
    }

    public Boolean getProbeTasksSuccessful() {
        return probeTasksSuccessful;
    }

    public void setProbeTasksSuccessful(Boolean probeTasksSuccessful) {
        this.probeTasksSuccessful = probeTasksSuccessful;
    }

    public String getProbeTasksTriggersSHA1() {
        return probeTasksTriggersSHA1;
    }

    public void setProbeTasksTriggersSHA1(String probeTasksTriggersSHA1) {
        this.probeTasksTriggersSHA1 = probeTasksTriggersSHA1;
    }

    public Boolean getBuildTasksSuccessful() {
        return buildTasksSuccessful;
    }

    public void setBuildTasksSuccessful(Boolean buildTasksSuccessful) {
        this.buildTasksSuccessful = buildTasksSuccessful;
    }

    public String getBuildTasksTriggersSHA1() {
        return buildTasksTriggersSHA1;
    }

    public void setBuildTasksTriggersSHA1(String buildTasksTriggersSHA1) {
        this.buildTasksTriggersSHA1 = buildTasksTriggersSHA1;
    }

    public Set<Dependency.Type> getBuildTypes() {
        return buildTypes;
    }

    public void setBuildTypes(Set<Dependency.Type> buildTypes) {
        this.buildTypes = buildTypes;
    }
}
