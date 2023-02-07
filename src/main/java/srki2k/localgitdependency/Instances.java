package srki2k.localgitdependency;

import org.gradle.api.Project;
import srki2k.localgitdependency.depenency.DependencyManager;
import srki2k.localgitdependency.extentions.SettingsExtension;
import srki2k.localgitdependency.property.PropertyManager;
import srki2k.localgitdependency.util.GradleApiManager;

public class Instances {
    private static Project project;
    private static SettingsExtension settingsExtension;
    private static DependencyManager dependencyManager;
    private static GradleApiManager gradleApiManager;
    private static PropertyManager propertyManager;

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

    public static GradleApiManager getGradleApiManager() {
        return gradleApiManager;
    }

    public static void setGradleApiManager(GradleApiManager gradleApiManager) {
        if (Instances.gradleApiManager != null) {
            Instances.gradleApiManager.disconnectAllGradleConnectors();
        }

        Instances.gradleApiManager = gradleApiManager;
    }

    public static PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public static void setPropertyManager(PropertyManager propertyManager) {
        Instances.propertyManager = propertyManager;
    }
}
