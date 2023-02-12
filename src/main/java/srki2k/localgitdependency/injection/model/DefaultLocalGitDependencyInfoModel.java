package srki2k.localgitdependency.injection.model;

import java.io.Serializable;
import java.util.List;

public class DefaultLocalGitDependencyInfoModel implements LocalGitDependencyInfoModel, Serializable {
    public static final long serialVersionUID = 1L;
    private final String projectId;
    private final String projectGradleVersion;
    private final boolean hasJavaPlugin;
    private final boolean hasMavenPublishPlugin;
    private final List<String> allJarTasksNames;
    private final List<String> allPublicationsNames;


    public DefaultLocalGitDependencyInfoModel(
            String projectId, String projectGradleVersion, boolean hasJavaPlugin,
            boolean hasMavenPublishPlugin, List<String> allJarTasksNames, List<String> allPublicationsNames) {
        this.projectId = projectId;
        this.projectGradleVersion = projectGradleVersion;
        this.hasJavaPlugin = hasJavaPlugin;
        this.hasMavenPublishPlugin = hasMavenPublishPlugin;
        this.allJarTasksNames = allJarTasksNames;
        this.allPublicationsNames = allPublicationsNames;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String projectGradleVersion() {
        return projectGradleVersion;
    }

    @Override
    public boolean hasJavaPlugin() {
        return hasJavaPlugin;
    }

    @Override
    public boolean hasMavenPublishPlugin() {
        return hasMavenPublishPlugin;
    }

    @Override
    public List<String> getAllJarTasksNames() {
        return allJarTasksNames;
    }

    // TODO: 07/02/2023 remove
    public List<String> getAllPublicationsNames() {
        return allPublicationsNames;
    }
}
