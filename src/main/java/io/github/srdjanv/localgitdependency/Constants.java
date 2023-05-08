package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Constants {
    private Constants() {
    }

    public static String PROJECT_VERSION = "@PROJECTVERSION@";
    public final static String EXTENSION_NAME = "LocalGitDependency";
    public final static String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public final static String JAVA_IMPLEMENTATION = "implementation";
    public final static String TAB_INDENT = "    ";
    public final static String TAB_INDENTX2 = TAB_INDENT + TAB_INDENT;

    //Custom Tasks generated for each dependency
    public final static String PROBE_ALL_DEPENDENCIES = "!ProbeAllDependencies";
    public final static Function<Dependency, String> PROBE_DEPENDENCY = s -> s.getName() + "-ProbeDependency";
    public final static String UNDO_ALL_LOCAL_GIT_CHANGES = "!UndoAllLocalGitChanges";
    public final static Function<Dependency, String> UNDO_LOCAL_GIT_CHANGES = s -> s.getName() + "-UndoLocalGitChanges";
    public final static String BUILD_ALL_GIT_DEPENDENCIES = "!BuildAllGitDependencies";
    public final static Function<Dependency, String> BUILD_GIT_DEPENDENCY = s -> s.getName() + "-BuildGitDependency";
    public final static String PRINT_ALL_DEPENDENCIES_INFO = "!PrintAllDependenciesInfo";
    public final static Function<Dependency, String> PRINT_DEPENDENCY_INFO = s -> s.getName() + "-PrintDependencyInfo";

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
    public static final Function<Dependency, String> RepositoryMavenProjectDependencyLocal = dependency -> dependency.getName() + "Repo";

    //Default plugin dirs
    public static final Function<Project, File> defaultDir = project -> new File(project.getLayout().getProjectDirectory().getAsFile(), "/localGitDependency");
    public static final Function<File, File> defaultPersistentDir = file -> new File(file, "/!data");
    public static final Function<File, File> defaultLibsDir = file -> new File(file, "/libs");
    public static final Function<File, File> defaultMavenFolder = file -> new File(file, "/!maven");

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

    public static void checkExistsAndMkdirs(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new UncheckedIOException(
                        new IOException(String.format("%s is not a directory, delete the file and refresh gradle", file.getAbsolutePath())));
            }
            return;
        }
        if (!file.mkdirs()) {
            throw new UncheckedIOException(
                    new IOException(String.format("Unable to create directory %s", file.getAbsolutePath())));
        }
    }

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");
    public static final BiFunction<File, String, File> concatFile = File::new;
    public static final String RepositoryMavenProjectLocal = "MavenProjectLocal";

}
