package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.srdjanv.localgitdependency.util.FileUtil.checkExistsAndMkdirs;

//Never change the name or package id
public final class Constants {
    private Constants() {
    }

    //Never change the name
    public static String PLUGIN_VERSION = "@PLUGIN_VERSION@";
    public final static String DEPENDENCY_BLOCK_EXTENSION_NAME = "lgd";
    public final static String EXTENSION_NAME = "LocalGitDependency";
    public final static String TASKS_GROUP = "LocalGitDependency";
    public final static String TASKS_GROUP_INTERNAL = "LocalGitDependency Internal";
    //Never change these 2 variables
    public final static String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public final static String LOCAL_GIT_DEPENDENCY_MANAGER_INSTANCE_EXTENSION = "LocalGitDependencyManagerInstance";
    public final static String JAVA_IMPLEMENTATION = "implementation";
    public final static String TAB_INDENT = "    ";
    public final static String TAB_INDENTX2 = TAB_INDENT + TAB_INDENT;

    //Custom Tasks generated for each dependency
    public final static String STARTUP_ALL_DEPENDENCIES = "!StartupAll";
    public final static Function<Dependency, String> STARTUP_DEPENDENCY = s -> s.getName() + "-Startup";
    public final static String PROBE_ALL_DEPENDENCIES = "!ProbeAll";
    public final static Function<Dependency, String> PROBE_DEPENDENCY = s -> s.getName() + "-Probe";
    public final static String UNDO_ALL_LOCAL_GIT_CHANGES = "!UndoAllLocalGitChanges";
    public final static Function<Dependency, String> UNDO_LOCAL_GIT_CHANGES = s -> s.getName() + "-UndoLocalGitChanges";
    public final static String BUILD_ALL_GIT_DEPENDENCIES = "!BuildAll";
    public final static Function<Dependency, String> BUILD_GIT_DEPENDENCY = s -> s.getName() + "-Build";
    public final static String PRINT_ALL_DEPENDENCIES_INFO = "!PrintAllInfo";
    public final static Function<Dependency, String> PRINT_DEPENDENCY_INFO = s -> s.getName() + "-PrintInfo";

    //Main plugin data
    public final static String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";
    public final static String PROJECT_DATA_JSON = "projectData.json";

    //Name generators
    public final static Function<PublicationData, String> PublicationTaskName = p -> {
        String publicationName = p.getPublicationName();

        return "publish" + publicationName.substring(0, 1).toUpperCase() + publicationName.substring(1) +
                "PublicationToMavenLocal";
    };
    public final static Function<PublicationData, String> FilePublicationTaskName = p -> {
        String publicationName = p.getPublicationName();
        String repositoryName = p.getRepositoryName();

        return "publish" + publicationName.substring(0, 1).toUpperCase() + publicationName.substring(1) + "PublicationTo" +
                repositoryName.substring(0, 1).toUpperCase() + repositoryName.substring(1) + "Repository";
    };

    public static final Function<Dependency, String> RepositoryFlatDir = dependency -> dependency.getName() + "FlatDir";
    public static final Function<SubDependencyData, String> RepositorySubFlatDir = dependency -> dependency.getName() + "FlatDir";
    public static final Function<Dependency, String> RepositoryMavenProjectDependencyLocal = dependency -> dependency.getName() + "Repo";
    public static final Function<SubDependencyData, String> RepositoryMavenProjectSubDependencyLocal = dependency -> dependency.getName() + "Repo";

    //Default plugin dirs
    public static final Function<Project, File> defaultDir = project -> new File(project.getLayout().getProjectDirectory().getAsFile(), "/localGitDependency");
    public static final Function<File, File> defaultPersistentDir = file -> new File(file, "/!data");
    public static final Function<File, File> defaultLibsDir = file -> new File(file, "/libs");
    public static final Function<File, File> defaultMavenFolder = file -> new File(file, "/!maven");


    public static final Function<Project, Directory> libsDir = project -> project.getLayout().getProjectDirectory().dir("/libs");
    public static final Function<Project, Directory> lgdDir = project -> project.getLayout().getBuildDirectory().dir( "/lgd").get();


    //Maven directory generators
    public static final Function<File, File> MavenProjectLocal = file -> {
        checkExistsAndMkdirs(file);
        return new File(file, "/!mavenProjectLocal");
    };
    public static final BiFunction<File, String, File> MavenProjectDependencyLocal = (file, name) -> {
        File maven = new File(file, "/!mavenProjectDependencyLocal" + "/" + name);
        checkExistsAndMkdirs(maven);
        return maven;
    };

    //Dependency data file generators
    public static final BiFunction<File, String, File> persistentInitScript = (persistentFolder, name) -> {
        File persistentInitScript = new File(persistentFolder, name + "/" + name + "Init.gradle");
        checkExistsAndMkdirs(persistentInitScript.getParentFile());
        return persistentInitScript;
    };
    public static final BiFunction<File, String, File> persistentJsonFile = (persistentFolder, name) -> {
        File persistentJsonFile = new File(persistentFolder, name + "/" + name + ".json");
        checkExistsAndMkdirs(persistentJsonFile.getParentFile());
        return persistentJsonFile;
    };

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");
    public static final BiFunction<File, String, File> concatFile = File::new;
    public static final String RepositoryMavenProjectLocal = "MavenProjectLocal";

}
