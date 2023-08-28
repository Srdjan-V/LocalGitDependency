package io.github.srdjanv.localgitdependency.ideintegration.adapters;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

public final class Adapter {
    public final static SourceDirectorySet JAVA = new JavaSourceDirectorySet();

    public interface SourceDirectorySet {
        Types getType();

        void configureSource(SourceSet sourceSet, SourceSetData sourceSetData, Project project);
    }

    public enum Types {
        Java
    }

}
