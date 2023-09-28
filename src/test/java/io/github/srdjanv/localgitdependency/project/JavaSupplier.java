package io.github.srdjanv.localgitdependency.project;

import java.io.File;
import org.gradle.internal.impldep.org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;

public class JavaSupplier {
    private static final File java8;

    static {
        Assertions.assertTrue(CoreMatchers.startsWith("1.8").matches(System.getProperty("java.version")));
        java8 = new File(System.getProperty("java.home"));
    }

    public static File getJava8() {
        return java8;
    }
}
