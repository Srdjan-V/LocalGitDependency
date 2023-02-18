package srki2k.localgitdependency.depenency;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.git.GitInfo;
import srki2k.localgitdependency.gradle.GradleInfo;
import srki2k.localgitdependency.persistence.PersistentInfo;
import srki2k.localgitdependency.property.Property;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dependency {
    private final String name;
    private final String configurationName;
    private final File mavenFolder;
    private final PersistentInfo persistentInfo;
    private final Type dependencyType;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;

    public Dependency(String configurationName, Property dependencyProperty) {
        Instances.getPropertyManager().applyDefaultProperty(dependencyProperty);

        this.name = dependencyProperty.getName() == null ? getNameFromUrl(dependencyProperty.getUrl()) : dependencyProperty.getName();
        this.configurationName = configurationName == null ? dependencyProperty.getDefaultConfiguration() : configurationName;
        this.dependencyType = dependencyProperty.getDependencyType();
        switch (dependencyType) {
            case MavenProjectLocal:
                this.mavenFolder = Constants.MavenProjectLocal.apply(dependencyProperty.getMavenFolder());
                break;

            case MavenProjectDependencyLocal:
                this.mavenFolder = Constants.MavenProjectDependencyLocal.apply(dependencyProperty.getMavenFolder(), name);
                break;

            default:
                this.mavenFolder = null;
        }

        this.gitInfo = new GitInfo(dependencyProperty, this);
        this.gradleInfo = new GradleInfo(dependencyProperty, this);
        this.persistentInfo = new PersistentInfo(dependencyProperty, this);
        validate();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getConfigurationName() {
        return configurationName;
    }

    @Nullable
    public File getMavenFolder() {
        return mavenFolder;
    }

    @NotNull
    public GitInfo getGitInfo() {
        return gitInfo;
    }

    @NotNull
    public GradleInfo getGradleInfo() {
        return gradleInfo;
    }

    @NotNull
    public PersistentInfo getPersistentInfo() {
        return persistentInfo;
    }

    @NotNull
    public Type getDependencyType() {
        return dependencyType;
    }

    // TODO: 18/02/2023 add all of the parameters for validation
    private void validate() {
        StringBuilder errors = null;

        if (gitInfo.getUrl() == null) {
            errors = crateStringBuilder(null);
            errors.append("Property: 'url' is not specified").append(System.lineSeparator());
        }
        if (name == null) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'name' is not specified").append(System.lineSeparator());
        }
        if (gitInfo.getDir().exists() && !gitInfo.getDir().isDirectory()) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'dir' is not a directory ").append(gitInfo.getDir()).append(System.lineSeparator());
        }

        if (errors != null) {
            throw new GradleException(errors.toString());
        }
    }

    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private StringBuilder crateStringBuilder(StringBuilder errors) {
        if (errors == null) {
            return new StringBuilder("Git dependency errors:").append(System.lineSeparator());
        }
        return errors;
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
    public enum Type {
        MavenLocal, //default maven local publishing
        MavenProjectLocal, //publishing to a maven inside the project file structure
        MavenProjectDependencyLocal, //same as MavenFileLocal except that every project has its own maven local folder
        JarFlatDir, //crates a flat dir repository at the build libs of the project
        Jar //directly add jar dependencies to the project
        // TODO: 18/02/2023 clean the build folder for the jar dependency, make it a task?
    }

}
