package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public interface IGradleManager {
    static IGradleManager createInstance(ProjectInstances projectInstances) {
        return new GradleManager(projectInstances);
    }
    void initGradleAPI();
    void buildDependencies();
    void buildDependency(Dependency dependency);
    void probeProject(Dependency dependency);
}
