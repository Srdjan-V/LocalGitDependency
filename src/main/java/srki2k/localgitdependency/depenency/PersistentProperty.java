package srki2k.localgitdependency.depenency;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.injection.model.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.property.Property;

import java.io.*;

public class PersistentProperty {
    private static final String currentWorkingDirSHA1 = "currentWorkingDirSHA1";
    private static final String latestGradleProbeResult = "LatestGradleProbeResult";
    private final Dependency dependency;
    private final File persistentFile;
    private String workingDirSHA1;
    private DefaultLocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel;

    private boolean dirty;

    public PersistentProperty(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyProperty.getPersistentFolder(), dependency.getName());

        if (persistentFile.exists() && persistentFile.isFile()) {
            try {
                loadFromJson();
            } catch (IOException | ParseException ignore) {
            }
        }
    }

    public Dependency getDependency() {
        return dependency;
    }

    public String getWorkingDirSHA1() {
        return workingDirSHA1;
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        this.dirty = true;
        this.workingDirSHA1 = workingDirSHA1;
    }

    private void loadFromJson() throws IOException, ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(persistentFile));
        workingDirSHA1 = (String) json.get(currentWorkingDirSHA1);

        //Map address = ((Map) json.get(latestGradleProbeResult));

    }

    public void saveToPersistentFile() {
        if (!dirty) return;

        if (!persistentFile.getParentFile().exists()) {
            persistentFile.getParentFile().mkdirs();
        }

        if (!persistentFile.exists()) {
            try {
                persistentFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        JSONObject jo = new JSONObject();

        // putting data to JSONObject
        jo.put(currentWorkingDirSHA1, workingDirSHA1);

        try (PrintWriter pw = new PrintWriter(persistentFile);) {
            pw.write(jo.toJSONString());
        } catch (FileNotFoundException ignore) {
        }
    }
}
