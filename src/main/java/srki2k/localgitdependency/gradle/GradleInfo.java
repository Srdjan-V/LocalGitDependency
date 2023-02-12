package srki2k.localgitdependency.gradle;

import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.property.Property;

import java.io.File;

public class GradleInfo {
    private final Dependency dependency;
    private final boolean manualBuild;
    private final boolean gradleProbeCashing;
    private final File initScript;

    public GradleInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.manualBuild = dependencyProperty.getManualBuild();
        this.gradleProbeCashing = dependencyProperty.getGradleProbeCashing();
        this.initScript = Constants.persistentInitScript.apply(dependencyProperty.getPersistentFolder(), dependency.getName());
    }

    public Dependency getDependency() {
        return dependency;
    }

    public boolean isManualBuild() {
        return manualBuild;
    }

    public boolean isGradleProbeCashing() {
        return gradleProbeCashing;
    }

    public File getInitScript() {
        return initScript;
    }
}
