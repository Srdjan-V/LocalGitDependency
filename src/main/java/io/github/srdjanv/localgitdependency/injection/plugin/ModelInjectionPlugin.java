package io.github.srdjanv.localgitdependency.injection.plugin;

import javax.inject.Inject;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

@SuppressWarnings("unused")
public final class ModelInjectionPlugin implements Plugin<Project> {
    private final ToolingModelBuilderRegistry registry;

    @Inject
    public ModelInjectionPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void apply(Project project) {
        if (project == project.getRootProject()) {
            registry.register(new LocalGitDependencyJsonInfoModelBuilder());
        }
    }
}
