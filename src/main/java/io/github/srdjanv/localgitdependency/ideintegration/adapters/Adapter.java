package io.github.srdjanv.localgitdependency.ideintegration.adapters;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

public final class Adapter {
    public static final SourceDirectorySet JAVA = new JavaSourceDirectorySet();

    public interface SourceDirectorySet {
        Types getType();

        void configure(SourceSet sourceSet, DirectorySetData directorySetData, Project project);
    }

    public enum Types {
        Java
    }
}
