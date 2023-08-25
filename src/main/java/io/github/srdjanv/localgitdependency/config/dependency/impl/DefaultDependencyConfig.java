package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DefaultDependencyConfig extends GroovyObjectSupport implements DependencyConfig, ConfigFinalizer {
    @Inject
    public DefaultDependencyConfig(String url, Managers managers) {
        getUrl().convention(url).finalizeValue();
        getName().convention(managers.getProject().provider(() -> getNameFromUrl(getUrl().get())));

        // TODO: 25/08/2023 conventions
    }

    @Override
    public void finalizeProps() {
    }



    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}
