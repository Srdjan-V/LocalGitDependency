package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultConfigsTest {

    @Test
    void testDefaultPluginConfigsFail() {
        var project = ProjectInstance.createProject();
        var lgdInstance = ProjectInstance.getLGDManager(project);
        var defaultDir = ((ConfigManager) lgdInstance.getConfigManager()).getDefaultDir();

        PluginConfig.Builder builder = new PluginConfig.Builder();
        builder.defaultDir(defaultDir);

        var plugin = new PluginConfig(builder, defaultDir);
        var errorList = ClassUtil.validateDataDefault(plugin);

        Assertions.assertNotEquals(0, errorList.size(), errorList.toString());
    }

    @Test
    void testDefaultPluginConfigs() {
        var project = ProjectInstance.createProject();
        var lgdInstance = ProjectInstance.getLGDManager(project);

        var plugin = ((ConfigManager) lgdInstance.getConfigManager()).defaultPluginConfig();
        var errorList = ClassUtil.validateDataDefault(plugin);

        Assertions.assertEquals(0, errorList.size(), errorList.toString());
    }

    @Test
    void testDefaultableConfigsFail() {
        DefaultableConfig.Builder builder = new DefaultableConfig.Builder();

        var plugin = new DefaultableConfig(builder);
        var errorList = ClassUtil.validateDataDefault(plugin);

        Assertions.assertNotEquals(0, errorList.size(), errorList.toString());
    }

    @Test
    void testDefaultableConfigs() {
        var project = ProjectInstance.createProject();
        var lgdInstance = ProjectInstance.getLGDManager(project);

        var plugin = ((ConfigManager) lgdInstance.getConfigManager()).defaultDefaultableConfig();
        var errorList = ClassUtil.validateDataDefault(plugin);

        Assertions.assertEquals(0, errorList.size(), errorList.toString());
    }


}
