package srki2k.localgitdependency;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Constants {
    public final static String EXTENSION_NAME = "LocalGitDependency";
    public final static String LOCAL_GIT_DEPENDENCY_EXTENSION = "localGitDependency";
    public final static String UNDO_LOCAL_GIT_CHANGES = "undoLocalGitChanges";
    public final static String BUILD_GIT_DEPENDENCIES = "buildGitDependencies";
    public final static String JAVA_IMPLEMENTATION = "implementation";


    public final static String MAIN_INIT_SCRIPT_GRADLE = "mainInitScript.gradle";


    public final static Function<String, String> PublicationName = s -> "LocalGitMavenDependencyForProject" + s;

    public final static Function<String, String> JarTaskName = s -> "LocalGitMavenTaskForProject" + s;

    public static final Supplier<File> defaultDir = () -> new File(Instances.getProject().getLayout().getProjectDirectory().getAsFile(), "/localGitDependency");

    public static final Function<File, File> defaultPersistentDir = file -> new File(file, "/!persistent");
    public static final Function<File, File> defaultLibsDir = file -> new File(file, "/libs");

    public static final BiFunction<File, String, File> persistentInitScript = (persistentFolder, name) -> new File(persistentFolder, name + "/" + name + "Init.gradle");
    public static final BiFunction<File, String, File> persistentJsonFile = (persistentFolder, name) -> new File(persistentFolder, name + "/" + name + ".json");

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");

    public static final BiFunction<File, String, File> concatFile = File::new;
}
