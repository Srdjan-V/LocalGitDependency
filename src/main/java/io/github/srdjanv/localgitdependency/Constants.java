package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import java.util.function.Function;

// Never change the name or package id
public final class Constants {
    private Constants() {}

    // Never change the name
    public static String PLUGIN_VERSION = "@PLUGIN_VERSION@";
    public static final String DEPENDENCY_BLOCK_EXTENSION_NAME = "lgd";
    public static final String EXTENSION_NAME = "LocalGitDependency";
    public static final String TASKS_GROUP = "LocalGitDependency";
    public static final String TASKS_GROUP_INTERNAL = "LocalGitDependency Internal";
    // Never change these 2 variables
    public static final String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public static final String LOCAL_GIT_DEPENDENCY_MANAGER_INSTANCE_EXTENSION = "LocalGitDependencyManagerInstance";
    public static final String JAVA_IMPLEMENTATION = "implementation";
    public static final String TAB_INDENT = "    ";
    public static final String TAB_INDENTX2 = TAB_INDENT + TAB_INDENT;

    // Custom Tasks generated for each dependency
    public static final String STARTUP_ALL_DEPENDENCIES = "!StartupAll";
    public static final Function<Dependency, String> STARTUP_DEPENDENCY = s -> s.getName() + "-Startup";
    public static final String PROBE_ALL_DEPENDENCIES = "!ProbeAll";
    public static final Function<Dependency, String> PROBE_DEPENDENCY = s -> s.getName() + "-Probe";
    public static final String UNDO_ALL_LOCAL_GIT_CHANGES = "!UndoAllLocalGitChanges";
    public static final Function<Dependency, String> UNDO_LOCAL_GIT_CHANGES = s -> s.getName() + "-UndoLocalGitChanges";
    public static final String BUILD_ALL_GIT_DEPENDENCIES = "!BuildAll";
    public static final Function<Dependency, String> BUILD_GIT_DEPENDENCY = s -> s.getName() + "-Build";
    public static final String PRINT_ALL_DEPENDENCIES_INFO = "!PrintAllInfo";
    public static final Function<Dependency, String> PRINT_DEPENDENCY_INFO = s -> s.getName() + "-PrintInfo";

    // Main plugin data
    public static final String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";
    public static final String PROJECT_DATA_JSON = "projectData.json";
}
