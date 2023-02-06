package srki2k.localgitdependency.depenency;

import org.gradle.api.GradleException;
import srki2k.localgitdependency.Constants;

import java.io.File;
import java.lang.reflect.Field;
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

    public Dependency(String configurationName, Property dependencyProperty, DefaultProperty property, DefaultProperty defaultGlobalProperty) {
        applyDefaultProperty(dependencyProperty, resolveProperty(property, defaultGlobalProperty));

        this.url = dependencyProperty.url;
        this.configurationName = configurationName == null ? dependencyProperty.name : configurationName;
        this.name = dependencyProperty.name == null ? getNameFromUrl(url) : dependencyProperty.name;
        this.commit = dependencyProperty.commit == null ? org.eclipse.jgit.lib.Constants.MASTER : dependencyProperty.commit;
        this.dir = Constants.concatFile.apply(dependencyProperty.dir, name);
        this.initScript = Constants.concatFile.apply(dependencyProperty.initScript, name + ".gradle");
        this.keepGitUpdated = dependencyProperty.keepGitUpdated;
        this.manualBuild = dependencyProperty.manualBuild;
        this.dependencyType = dependencyProperty.dependencyType;

        validateDependency();
    }

    //applies missing dependencyProperty from the globalProperty
    private void applyDefaultProperty(Property dependencyProperty, DefaultProperty property) {
        for (Field field : CommonProperty.class.getDeclaredFields()) {
            try {
                if (field.get(dependencyProperty) == null) {
                    field.set(dependencyProperty, field.get(property));
                }
            } catch (Exception e) {
                throw new GradleException("Unexpected error while reflecting CommonProperty class", e);
            }
        }
    }

    //applies missing globalProperty from the defaultGlobalProperty
    private DefaultProperty resolveProperty(DefaultProperty globalProperty, DefaultProperty defaultGlobalProperty) {
        if (globalProperty == null) {
            return defaultGlobalProperty;
        }

        DefaultProperty resolvedProperty = new DefaultProperty();
        for (Field field : CommonProperty.class.getDeclaredFields()) {
            try {
                Object globalPropertyField = field.get(globalProperty);
                Object defaultGlobalPropertyField = field.get(defaultGlobalProperty);

                if (globalPropertyField == null) {
                    field.set(resolvedProperty, defaultGlobalPropertyField);
                } else {
                    field.set(resolvedProperty, globalPropertyField);
                }

            } catch (Exception e) {
                throw new GradleException("Unexpected error while reflecting CommonProperty class", e);
            }
        }

        return resolvedProperty;
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

    //Property's that only a dependency can have
    public static class Property extends CommonProperty {
        private final String url;

        //Dependency name
        private String name;
        private String commit;

        public Property(String url) {
            this.url = url;
        }

        public void name(String name) {
            this.name = name;
        }

        public void commit(String commit) {
            this.commit = commit;
        }

        public void branch(String branch) {
            this.commit = branch;
        }

        public void tag(String tag) {
            this.commit = tag;
        }
    }

    //Property's for global configuration
    public static class DefaultProperty extends CommonProperty {
        public DefaultProperty() {
        }
    }

    //Base property's for dependency's global configurations
    private abstract static class CommonProperty {
        String defaultConfiguration;
        Boolean manualBuild;
        Boolean keepGitUpdated;
        File dir;
        File initScript;

        DependencyType dependencyType;


        private CommonProperty() {
        }

        public void defaultConfiguration(String defaultConfiguration) {
            this.defaultConfiguration = defaultConfiguration;
        }

        public void manualBuild(boolean manualBuild) {
            this.manualBuild = manualBuild;
        }

        public void keepGitUpdated(boolean keepGitUpdated) {
            this.keepGitUpdated = keepGitUpdated;
        }

        public void dir(File dir) {
            this.dir = dir;
        }

        public void initScript(File initScript) {
            this.initScript = initScript;
        }

        public void dependencyType(DependencyType dependencyType) {
            this.dependencyType = dependencyType;
        }
    }

    //Type of the crated dependency
    public enum DependencyType {
        MavenLocal,
        Jar
    }

}
