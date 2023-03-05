package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.property.DefaultProperty;
import com.srdjanv.localgitdependency.property.Property;
import groovy.lang.Closure;
import org.junit.jupiter.api.Test;

public class PropertyTests {

    @Test
    void Test() {
        ProjectInstance.createProject();
        Closure<DefaultProperty.Builder> propertyClosure = new Closure<DefaultProperty.Builder>(null) {
            public DefaultProperty.Builder doCall() {
                DefaultProperty.Builder builder = (DefaultProperty.Builder) getDelegate();
                builder.gitDir("./git");
                builder.javaHomeDir("./jdk");
                builder.mavenFolder("./maven");
                return builder;
            }
        };

        //Instances.getSettingsExtension().configureGlobal(propertyClosure);
    }

}
