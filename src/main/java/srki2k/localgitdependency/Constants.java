package srki2k.localgitdependency;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Constants {
    public static String PROJECT_VERSION = "@PROJECTVERSION@";
    public final static String EXTENSION_NAME = "LocalGitDependency";
    public final static String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public final static String UNDO_LOCAL_GIT_CHANGES = "undoLocalGitChanges";
    public final static String BUILD_GIT_DEPENDENCIES = "buildGitDependencies";
    public final static String JAVA_IMPLEMENTATION = "implementation";


    public final static String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";


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
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException(String.format("Unable to create directory %s", file.getAbsolutePath()));
            }
        }

        return new File(file, "/!mavenProjectLocal");
    };
    public static final BiFunction<File, String, File> MavenProjectDependencyLocal = (file, name) -> {
        File maven = new File(file, "/!mavenProjectDependencyLocal" + "/" + name);
        if (!maven.exists()) {
            if (!maven.mkdirs()) {
                throw new RuntimeException(String.format("Unable to create directory %s", maven.getAbsolutePath()));
            }
        }
        return maven;
    };
    public static final Function<File, File> defaultMavenLocalFolderUrl = file -> new File("file://", file.getAbsolutePath());
    public static final BiFunction<File, String, File> persistentInitScript = (persistentFolder, name) -> {
        File persistentInitScript = new File(persistentFolder, name + "/" + name + "Init.gradle");
        if (!persistentInitScript.exists()) {
            persistentInitScript.getParentFile().mkdirs();
            try {
                persistentInitScript.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return persistentInitScript;
    };
    public static final BiFunction<File, String, File> persistentJsonFile = (persistentFolder, name) -> {
        File persistentJsonFile = new File(persistentFolder, name + "/" + name + ".json");
        if (!persistentJsonFile.exists()) {
            persistentJsonFile.getParentFile().mkdirs();
            try {
                persistentJsonFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return persistentJsonFile;
    };

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");

    public static final BiFunction<File, String, File> concatFile = File::new;


    public static final String RepositoryMavenProjectLocal = "MavenProjectLocal";
    public static final Function<String, String> RepositoryFlatDir = name -> name + "FlatDir";
    public static final Function<String, String> RepositoryMavenProjectDependencyLocal = name -> name + "Repo";

}
