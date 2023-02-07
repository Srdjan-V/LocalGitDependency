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



    public static final Supplier<File> defaultLibDirs = () -> new File(Instances.getProject().getLayout().getProjectDirectory().getAsFile(), "/libs");

    public static final Function<File, File> defaultInitScriptDirs = file -> new File(file, "/initScripts");

    public static final Function<File, File> buildDir = file -> new File(file, "/build/libs");

    public static final BiFunction<File, String, File> concatFile = File::new;
}
