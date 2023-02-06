package srki2k.localgitdependency;

import org.gradle.api.Project;
import srki2k.localgitdependency.depenency.DependencyManager;
import srki2k.localgitdependency.extentions.SettingsExtension;

public class Instances {
    private static Project project;
    private static SettingsExtension settingsExtension;
    private static DependencyManager dependencyManager;

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
}
