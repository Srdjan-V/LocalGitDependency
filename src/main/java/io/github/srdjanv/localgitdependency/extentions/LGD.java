package io.github.srdjanv.localgitdependency.extentions;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.Action;
import org.gradle.internal.Actions;

public final class LGD {
    public static final String NAME = "lgd";

    private final Managers managers;
    public LGD(Managers managers) {
        this.managers = managers;
    }

    public void plugin(Action<PluginConfig> action) {
        action.execute(managers.getConfigManager().getPluginConfig());
    }
    public void defaults(Action<DefaultableConfig> action) {
        action.execute(managers.getConfigManager().getDefaultableConfig());
    }

    public void register(String dependencyURL) {
        register(dependencyURL, Actions.doNothing());
    }

    public void register(String dependencyURL, Action<DependencyConfig> action) {
       action.execute( managers.getDependencyManager().registerDependency(dependencyURL));
    }

}
