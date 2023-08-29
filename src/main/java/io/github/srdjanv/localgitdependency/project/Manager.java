package io.github.srdjanv.localgitdependency.project;

public interface Manager extends Managers {
    Managers getProjectManagers();

    String getManagerName();
}
