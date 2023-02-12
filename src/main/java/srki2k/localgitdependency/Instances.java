package srki2k.localgitdependency;

import org.gradle.api.Project;
import srki2k.localgitdependency.depenency.DependencyManager;
import srki2k.localgitdependency.extentions.SettingsExtension;
import srki2k.localgitdependency.git.GitManager;
import srki2k.localgitdependency.property.PropertyManager;
import srki2k.localgitdependency.gradle.GradleManager;

public class Instances {
    private static Project project;
    private static SettingsExtension settingsExtension;
    private static DependencyManager dependencyManager;
    private static GradleManager gradleManager;
    private static PropertyManager propertyManager;
    private static GitManager gitManager;

    public static Project getProject() {
        return project;
    }

    public static void setProject(Project settings) {
        Instances.project = settings;
    }

    public static SettingsExtension getSettingsExtension() {
        return settingsExtension;
    }

    public static void setSettingsExtension(SettingsExtension settingsExtension) {
        Instances.settingsExtension = settingsExtension;
    }

    public static DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public static void setDependencyManager(DependencyManager dependencyManager) {
        Instances.dependencyManager = dependencyManager;
    }

    public static GradleManager getGradleApiManager() {
        return gradleManager;
    }

    public static void setGradleApiManager(GradleManager gradleManager) {
        if (Instances.gradleManager != null) {
            Instances.gradleManager.disconnectAllGradleConnectors();
        }

        Instances.gradleManager = gradleManager;
    }

    public static PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public static void setPropertyManager(PropertyManager propertyManager) {
        Instances.propertyManager = propertyManager;
    }

    public static GitManager getGitManager() {
        return gitManager;
    }

    public static void setGitManager(GitManager gitManager) {
        Instances.gitManager = gitManager;
    }
}
