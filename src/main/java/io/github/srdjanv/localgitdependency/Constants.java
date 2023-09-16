package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import java.util.function.Function;

public final class Constants {
    private Constants() {}

    // Never change the name
    public static String PLUGIN_VERSION = "@PLUGIN_VERSION@";
    public static final String PLUGIN_NAME = "LocalGitDependency";
    public static final String TASKS_GROUP = "LocalGitDependency";
    public static final String TASKS_GROUP_INTERNAL = "LocalGitDependency Internal";
    public static final String JAVA_IMPLEMENTATION = "implementation";
    public static final String TAB_INDENT = "    ";
    public static final String TAB_INDENTX2 = TAB_INDENT + TAB_INDENT;

    // Custom Tasks generated for each dependency
    public static final String STARTUP_ALL_DEPENDENCIES = "!StartupAll";
    public static final Function<Dependency, String> STARTUP_DEPENDENCY = s -> s.getName() + "-Startup";
    public static final String PROBE_ALL_DEPENDENCIES = "!ProbeAll";
    public static final Function<Dependency, String> PROBE_DEPENDENCY = s -> s.getName() + "-Probe";
    public static final String BUILD_ALL_GIT_DEPENDENCIES = "!BuildAll";
    public static final Function<Dependency, String> BUILD_GIT_DEPENDENCY = s -> s.getName() + "-Build";

    // Main plugin data
    public static final String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";
    public static final String PROJECT_DATA_JSON = "projectData.json";
}
