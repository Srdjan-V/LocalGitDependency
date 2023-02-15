package srki2k.localgitdependency.depenency;

import org.gradle.api.GradleException;
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
    private final File mavenLocalFolder;
    private final PersistentInfo persistentInfo;
    private final DependencyType dependencyType;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;

    public Dependency(String configurationName, Property dependencyProperty) {
        Instances.getPropertyManager().applyDefaultProperty(dependencyProperty);

        this.name = dependencyProperty.getName() == null ? getNameFromUrl(dependencyProperty.getUrl()) : dependencyProperty.getName();
        this.configurationName = configurationName == null ? dependencyProperty.getDefaultConfiguration() : configurationName;
        this.mavenLocalFolder = dependencyProperty.getMavenLocalFolder();
        this.dependencyType = dependencyProperty.getDependencyType();
        this.gitInfo = new GitInfo(dependencyProperty, this);
        this.gradleInfo = new GradleInfo(dependencyProperty, this);
        this.persistentInfo = new PersistentInfo(dependencyProperty, this);
        validate();
    }

    public String getName() {
        return name;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public File getMavenLocalFolder() {
        return mavenLocalFolder;
    }

    public GitInfo getGitInfo() {
        return gitInfo;
    }

    public GradleInfo getGradleInfo() {
        return gradleInfo;
    }

    public PersistentInfo getPersistentInfo() {
        return persistentInfo;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

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
    public enum DependencyType {
        MavenLocal,
        MavenFileLocal,
        Jar

    }

}
