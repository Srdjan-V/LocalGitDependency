package io.github.srdjanv.localgitdependency;

import org.gradle.api.GradleException;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Constants {
    public static String PROJECT_VERSION = "@PROJECTVERSION@";
    public final static String EXTENSION_NAME = "LocalGitDependency";
    public final static String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public final static String UNDO_ALL_LOCAL_GIT_CHANGES = "!UndoAllLocalGitChanges";
    public final static Function<String, String> UNDO_LOCAL_GIT_CHANGES = s -> s + "-UndoLocalGitChanges";
    public final static String BUILD_ALL_GIT_DEPENDENCIES = "!BuildAllGitDependencies";
    public final static Function<String, String> BUILD_GIT_DEPENDENCY = s -> s + "-BuildGitDependency";
    public final static String PRINT_ALL_DEPENDENCIES_INFO = "!PrintAllDependenciesInfo";
    public final static Function<String, String> PRINT_DEPENDENCY_INFO = s -> s + "-PrintDependencyInfo";
    public final static String JAVA_IMPLEMENTATION = "implementation";
    public final static String TAB_INDENT = "    ";


    public final static String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";
    public final static String PROJECT_DATA_JSON = "projectData.json";

    public final static Function<String, String> MavenPublicationName = s -> "InitScriptPublicationForProject" + s;
    public final static Function<String, String> MavenRepositoryName = s -> "InitScriptRepositoryForProject" + s;
    public final static Function<String, String> PublicationTaskName = s -> "publish" +
            s.substring(0, 1).toUpperCase() + s.substring(1) +
            "PublicationToMavenLocal";

    public final static BiFunction<String, String, String> FilePublicationTaskName = (p, m) -> "publish" +
            p.substring(0, 1).toUpperCase() + p.substring(1) +
            "PublicationTo" +
            m.substring(0, 1).toUpperCase() + m.substring(1) +
            "Repository";

    public final static Function<String, String> JarSourceTaskName = s -> "InitScriptSourceTaskForProject" + s;
    public final static Function<String, String> JarJavaDocTaskName = s -> "InitScriptJavaDocTaskForProject" + s;

    public static final Supplier<File> defaultDir = () -> new File(Instances.getProject().getLayout().getProjectDirectory().getAsFile(), "/localGitDependency");

    public static final Function<File, File> defaultPersistentDir = file -> new File(file, "/!data");
    public static final Function<File, File> defaultLibsDir = file -> new File(file, "/libs");

    public static final Function<File, File> defaultMavenFolder = file -> new File(file, "/!maven");
    public static final Function<File, File> MavenProjectLocal = file -> {
        checkExistsAndMkdirs(file);
        return new File(file, "/!mavenProjectLocal");
    };
    public static final BiFunction<File, String, File> MavenProjectDependencyLocal = (file, name) -> {
        File maven = new File(file, "/!mavenProjectDependencyLocal" + "/" + name);
        checkExistsAndMkdirs(maven);
        return maven;
    };

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

    public static void checkExistsAndMkdirs(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new GradleException(String.format("%s is not a directory, delete the file and refresh gradle", file.getAbsolutePath()));
            }
            return;
        }
        if (!file.mkdirs()) {
            throw new GradleException(String.format("Unable to create directory %s", file.getAbsolutePath()));
        }
    }

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");

    public static final BiFunction<File, String, File> concatFile = File::new;


    public static final String RepositoryMavenProjectLocal = "MavenProjectLocal";
    public static final Function<String, String> RepositoryFlatDir = name -> name + "FlatDir";
    public static final Function<String, String> RepositoryMavenProjectDependencyLocal = name -> name + "Repo";

}
