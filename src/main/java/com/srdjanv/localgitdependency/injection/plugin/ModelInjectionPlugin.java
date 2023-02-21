package com.srdjanv.localgitdependency.injection.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class ModelInjectionPlugin implements Plugin<Project> {
    private final ToolingModelBuilderRegistry registry;

    @Inject
    public ModelInjectionPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void apply(Project project) {
        if (project == project.getRootProject()) {
            registry.register(new LocalGitDependencyInfoModelBuilder());
        }
    }

}
