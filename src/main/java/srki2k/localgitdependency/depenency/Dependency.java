package srki2k.localgitdependency.depenency;

import org.gradle.api.GradleException;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.property.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dependency {
    private final String url;
    private final String configurationName;
    private final String name;
    private final String commit;
    private final File dir;
    private final File initScript;
    private final boolean keepGitUpdated;
    private final boolean manualBuild;
    private final DependencyType dependencyType;
    private List<Exception> gitExceptions;

    public Dependency(String configurationName, Property dependencyProperty) {
        Instances.getPropertyManager().applyDefaultProperty(dependencyProperty);

        this.url = dependencyProperty.getUrl();
        this.configurationName = configurationName == null ? dependencyProperty.getName() : configurationName;
        this.name = dependencyProperty.getName() == null ? getNameFromUrl(url) : dependencyProperty.getName();
        this.commit = dependencyProperty.getCommit() == null ? org.eclipse.jgit.lib.Constants.MASTER : dependencyProperty.getCommit();
        this.dir = Constants.concatFile.apply(dependencyProperty.getDir(), name);
        this.initScript = Constants.concatFile.apply(dependencyProperty.getInitScript(), name + ".gradle");
        this.keepGitUpdated = dependencyProperty.getKeepGitUpdated();
        this.manualBuild = dependencyProperty.getManualBuild();
        this.dependencyType = dependencyProperty.getDependencyType();

        validateDependency();
    }

    private void validateDependency() {
        StringBuilder errors = null;

        if (url == null) {
            errors = crateStringBuilder(null);
            errors.append("Property: 'url' is not specified").append(System.lineSeparator());
        }
        if (name == null) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'name' is not specified").append(System.lineSeparator());
        }
        if (dir.exists() && !dir.isDirectory()) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'dir' is not a directory ").append(dir).append(System.lineSeparator());
        }

        if (errors != null) {
            throw new GradleException(errors.toString());
        }
    }

    private StringBuilder crateStringBuilder(StringBuilder errors) {
        if (errors == null) {
            return new StringBuilder("Git dependency errors:").append(System.lineSeparator());
        }
        return errors;
    }

    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getCommit() {
        return commit;
    }

    public File getDir() {
        return dir;
    }

    public File getInitScript() {
        return initScript;
    }

    public boolean isKeepGitUpdated() {
        return keepGitUpdated;
    }

    public boolean isManualBuild() {
        return manualBuild;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public boolean hasGitExceptions() {
        return !gitExceptions.isEmpty();
    }

    public List<Exception> getGitExceptions() {
        return gitExceptions;
    }

    public void addGitExceptions(Exception gitException) {
        List<Exception> exceptions = createList(this.gitExceptions);
        exceptions.add(gitException);
    }

    private static List<Exception> createList(List<Exception> o) {
        if (o == null) {
            return new ArrayList<>();
        }
        return o;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    //Type of the crated dependency
    public enum DependencyType {
        MavenLocal,
        Jar
    }

}
